package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.consensome;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import edu.bcm.dldcc.big.nursa.model.omics.dto.ConsensomeDataWrap;
import edu.bcm.dldcc.big.nursa.model.omics.dto.ConsensomeResult;
import edu.bcm.dldcc.big.nursa.model.omics.Consensome;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.TransSummary;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.ConsensomeData;
import edu.bcm.dldcc.big.nursa.services.rest.omics.ConsensomeFilesBean;
import edu.bcm.dldcc.big.nursa.services.utils.InputStreamWithFileDeletion;
import edu.bcm.dldcc.big.nursa.util.filter.BaseFilter;

/**
 * REST Service for Consensome data
 * @author mcowiti
 *
 */
public class ConsensomeServiceRestBean implements ConsensomeService {

	public static int UI_DATA_MAX=1000;
	public static int XLS_DATA_MAX=20000;
	
	final DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm");
	private static final String inputMessage="Provide pathway/physiologicalSystem/Organ/Species to find a Consensome";
	private static final String inputMessageDoi="Provide a valid DOI to find a Consensome";
	
	public static final String tooManyMessage="More than "+UI_DATA_MAX +" records. Please consider file download of Consensome data";
	private static final Object pageNeedCountMessage = "If you supply page, you must provide a countDatapoints";
	
	@Context
    private ServletContext context;
    
	@Inject
	private ConsensomeFilesBean fileBean;
	
	
	@Inject
	private TranscriptomicConsensomeBean consensomeBean;
	
	private static Logger log = Logger.getLogger(ConsensomeServiceRestBean.class.getName());

	/**
	 * Find all active summaries
	 */
	public Response findSummaries() {
	    List<TransSummary> list=this.consensomeBean.findSummaries();
		 return Response.ok().entity(list).build();
	} 
	
	/**
	 * Find current active summary
	 * TODO if version(date) supplied, find archived summary
	 * TODO We need to provide separate service for archived data, as may require mostly download
	 */
	public Response findSummary(String pw,String ps,String o,String s,
			String v) {
   	 
		if(pw == null || ps == null || o == null || s == null)
			return Response.status(400).entity(inputMessage).build();
		
		 List<TransSummary> list=this.consensomeBean.findSummary(pw, ps, o, s);
		 return Response.ok().entity(list).build();
	    } 
	
	public Response findSummary(String doi) {
   	 
		if(doi == null )
			return Response.status(400).entity(inputMessageDoi).build();
		
		if(doi != null){
			BaseFilter f= new BaseFilter();
			if(!f.cleanDoi(doi))
				return Response.status(400).entity(inputMessageDoi).build();
		}
		 List<TransSummary> list=this.consensomeBean.findSummary(doi);
		 return Response.ok().entity(list).build();
	  } 
	
	public Response countConsensome(String doi,String pw,String ps,String o,String s,String v) {
	   
		if(doi == null)
			if(pw == null || ps == null || o == null || s == null)
				return Response.status(400).entity(inputMessage).build();
		
		if(doi != null){
			BaseFilter f= new BaseFilter();
			if(!f.cleanDoi(doi))
				return Response.status(400).entity(inputMessageDoi).build();
		}
		int count=this.consensomeBean.countConsensomes(doi,pw, ps, o, s);
		return Response.ok().entity(count).build();
	} 
	
	/**
	 * Find current data meeting params
	 * If version(date) supplied, find archived data
	 *  TODO We need to provide separate service for archived data, as may require mostly download
	 */
	public Response findConsensome(String doi,String pw,String ps,String o,String s,
			Integer count,String v) {
	   
		if(doi == null)
			if(pw == null || ps == null || o == null || s == null)
				return Response.status(400).entity(inputMessage).build();
		
		if(doi != null){
			BaseFilter f= new BaseFilter();
			if(!f.cleanDoi(doi))
				return Response.status(400).entity(inputMessageDoi).build();
		}
		
		int max=UI_DATA_MAX;
		if(count != null){
			max=(count.intValue() > UI_DATA_MAX)?max:count.intValue();
		}
		
		
		ConsensomeResult result=this.consensomeBean.findConsensomesResult(doi,pw, ps, o, s,max,0);
		return Response.ok().entity(result.getResults()).build();
	} 
	
	
	public Response findConsensomesWithCount(String doi,String pw,String ps,String o,String s,
			Integer count,Integer page,String v) {
	   
		if(doi == null)
			if(pw == null || ps == null || o == null || s == null)
				return Response.status(400).entity(inputMessage).build();
		
		if(page != null && count == null){
			return Response.status(400).entity(pageNeedCountMessage).build();
		}
		
		if(doi != null){
			BaseFilter f= new BaseFilter();
			if(!f.cleanDoi(doi))
				return Response.status(400).entity(inputMessageDoi).build();
		}
		
		//TODO Most Consensome data will be >1000
		//there is no way to (prompt user) to filter, so will always return max 1000
		//if(total > UI_DATA_MAX)return Response.status(413).entity(tooManyMessage).build();
		
		//OPTMZ cache these results
		int total=this.consensomeBean.countConsensomes(doi,pw, ps, o, s);
		
		int max=UI_DATA_MAX;
		if(count != null){
			max=(count.intValue() > UI_DATA_MAX)?max:count.intValue();
		}
		
		int start=0;
		if(page != null){
			start=page.intValue() * max;
		}
		
		log.log(Level.FINE,"start="+start);
		ConsensomeDataWrap<Consensome> data=null;
		ConsensomeResult<Consensome> result=this.consensomeBean.findConsensomesResult(doi,pw, ps, o, s,max,start);
		if(result!=null)
			data= new ConsensomeDataWrap<Consensome>(total,(TransSummary) result.getSummary(),result.getResults());
		return Response.ok().entity(data).build();
	} 
	
	
	public Response downloadExcelFile(String pw,String ps,String o,String s,String v, Integer count,String email) {
        
		if(pw == null || ps == null || o == null || s == null)
			return Response.status(400).entity(inputMessage).build();
		
		//TODO if email!=null, use email method
		
		InputStream targetStream = null;
        List<ConsensomeData> list=null;
        String fileName=formFileName(pw, ps, o, s);
        try {
        	log.log(Level.FINE,"consensome excel pw="+pw+" ps="+ps+" o="+o+" s="+s+" v="+v);
        	int max=XLS_DATA_MAX;
        	if(count != null){
        		max=(count.intValue() < XLS_DATA_MAX)?count:max;
        	}
        	long b=System.currentTimeMillis();
        	list=this.consensomeBean.findConsensomeData(pw, ps, o, s,max);
        	if(list!=null && list.size()>10000)
        		log.log(Level.WARNING, "Excel data in ms "+(System.currentTimeMillis()-b));
        	
        	String query=formQueryName(pw,ps,o,s);	
            File file = null;//TODO this.fileBean.generateTranscriptomicConsensomeExcelData(query,null,list,max);
            if(list!=null && list.size() > 10000)
            	log.log(Level.WARNING, "Excel file generated in ms "+(System.currentTimeMillis()-b));
            list=null;	
            targetStream = new InputStreamWithFileDeletion(file);
            
            Response.ResponseBuilder response = Response.ok().entity((Object) targetStream);
            response.header("Content-Disposition","attachment; filename=" + fileName + ".xls");
          	
            return response.build();
            
        } catch (Exception e) {
            log.log(Level.WARNING, "Cannot write results to the temp file. Will send the error file", e);
            targetStream = context.getResourceAsStream("/WEB-INF/error.xls");
            
            Response.ResponseBuilder response = Response.ok().entity((Object) targetStream);
            response.header("Content-Disposition","attachment; filename=" + fileName + ".xls");
          	return response.build();
        }
     }
	
	private String formFileName(String pw,String ps,String o,String s){
		
		String time=LocalDateTime.now().format(dtformatter);
		
		StringBuilder sb= new StringBuilder();
		return sb.append((pw.length()>10)?pw.substring(0, 9):pw).append("_")
		.append((ps.length()>15)?ps.substring(0, 14):ps).append("_")
		.append((o.length()>10)?o.substring(0, 9):o).append("_")
		.append((s.length()>10)?s.substring(0, 9):s).append("_").append(time)
		.toString();
	}
	
	private String formQueryName(String pw,String ps,String o,String s){
		
		return new StringBuilder(pw).append("+")
				.append(ps).append("+").append(o).append("+").append(s).toString();	
	}

}
