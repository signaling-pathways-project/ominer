package edu.bcm.dldcc.big.nursa.services.rest.omics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


import edu.bcm.dldcc.big.nursa.model.cistromic.BindingScore;
import edu.bcm.dldcc.big.nursa.model.cistromic.BindingScore_;
import edu.bcm.dldcc.big.nursa.model.cistromic.CisConsensome;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SimpleQueryForm;
import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.*;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.QueryParametersData;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.TmQueryResponse;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.ApisCacheManager;
import edu.bcm.dldcc.big.nursa.services.utils.FileHelper;
import edu.bcm.dldcc.big.nursa.services.utils.InputStreamWithFileDeletion;
import edu.bcm.dldcc.big.nursa.util.filter.BaseFilter;

/**
 * Cistromics dataset Service Bean
 * Provides info for Datasets, binding scores datapoints and Consensome target genes.
 * @author mcowiti
 *
 */
public class CistromicsRestServiceBean implements CistromicsRestService {
	
	@PersistenceContext(unitName = "NURSA")
	private EntityManager em;


	@Inject
	private OmicsServicebean omicsServicebean;

	@Inject
	private FileHelper fileHelper;

	@Inject
	private ConsensomeFilesBean consensomeFilesBean;
	
	@Context
    private ServletContext context;


	 private static Logger log = Logger.getLogger(CistromicsRestServiceBean.class.getName());


	@Override
	public Response findConsensomesWithCount(String doi, String familyId, String ps, String organ, String species,
			double percentile,Integer count, Integer page, String date) {

		SimpleQueryForm sform=this.omicsServicebean.formSimpleQueryForm(QueryType.Cistromic.toString(),"consensome", null,doi,
				ps, organ,null,species, null, familyId,
				null,null,null,percentile);

		 if(doi == null) {
			 if (familyId == null && ps == null && organ == null && species == null)
				 return Response.status(400).entity(OmicsServicebean.inputMessage).build();
		 }
			
		if(page != null && count == null){
			return Response.status(400).entity(OmicsServicebean.pageNeedCountMessage).build();
		}
			
		if(doi != null){
			BaseFilter f= new BaseFilter();
			if(!f.cleanDoi(doi))
				return Response.status(400).entity(OmicsServicebean.inputMessageDoi).build();
		}else{

            Map<String,String> input=this.omicsServicebean.recastFamilyPsOrganOfNull(familyId,ps,organ);
            familyId=input.get("family");
            ps=input.get("ps");
            organ=input.get("organ");

		}
			
			//TODO Most Consensome data will be >1000
			//there is no way to (prompt user) to filter, so will always return max 1000
			//if(total > UI_DATA_MAX)return Response.status(413).entity(tooManyMessage).build();

		QueryParametersData queryParametersData=this.omicsServicebean.formOldQueryForm(
				"consensome",null,doi,ps,organ,null,null,species,
				null,familyId,
				null,null,null,null,null);

		QueryForm queryForm= new QueryForm();
		queryForm.setQueryParameter(queryParametersData);
		this.omicsServicebean.persistQueryId(sform,queryForm);

		int total=this.omicsServicebean.countConsensomes(QueryType.Cistromic,doi,familyId, ps, organ, species,percentile);

		//log.log(Level.FINE, "consensomes countDatapoints ="+total);
			
			
			int max=CistromicsRestService.UI_DATA_MAX;
			if(count != null){
				max=(count > CistromicsRestService.UI_DATA_MAX)?max:count;
			}
			
			int startPage=0;
			if(page != null){
				startPage=page * max;
			}

			ConsensomeDataWrap<CisConsensome> data=null;
			ConsensomeResult<CisConsensome> result=omicsServicebean.findConsensomeResult(QueryType.Cistromic,doi,familyId, ps, organ, species,percentile,max, startPage, date);
			
			if(result != null) {
				data = new ConsensomeDataWrap<CisConsensome>(total, (ConsensomeSummary) result.getSummary(), result.getResults());
				data.setQueryForm(queryForm);
                return Response.ok().entity(data).build();
			}

			return Response.status(404).entity("No Cistromic Consensome with that criteria").build();
	}
	 
	 
	 @Override
	 public Response downloadCisConsensomeDataAsExcelFile(String family, String ps, String organ, String species, String version,
													  String email) {
		 return Response.ok().entity("Download by Consensome key not implemented yet").build();
	 }


	 public Response downloadConsensomeFileByDoiOrQueryId(String queryId,String doi,double percentile){
		 InputStream targetStream = null;
		 if((doi == null || doi.length() == 0) && (queryId == null || queryId.length() == 0))
			 return Response.status(400).entity("Need DOI or QueryId to download a File").build();

		 try {

			 List<CisConsensome> datalist=null;
			 String query=null;

			 if(doi == null || doi.length() == 0) {

				if (queryId == null || queryId.length() == 0)
					return Response.status(404).entity("Please provide an existing queryId or DOI to download").build();

				Optional<SimpleQueryForm> form = omicsServicebean.getSimpleQueryForm(queryId);

				if (!form.isPresent()) {
					log.log(Level.WARNING, "This file download link expired:" + queryId);
					return Response.status(404).entity(queryId + " : This file download link expired. Please re-submit the query to get a new File download link.").build();
				}
				SimpleQueryForm queryForm = form.get();
				datalist = omicsServicebean.findCisConsensomes(false,true,queryForm.signalingPathway,null,
						queryForm.ps,
						queryForm.organ,
						queryForm.species,queryForm.percentile,
						OmicsServicebean.MAX_NUMBER_XLS, 0,null);

				 if(datalist.size()>0) {
					 doi = datalist.get(0).getKey().getDoi();
					 query = datalist.get(0).getKey().toString();
				 }
			}else{

				 BaseFilter f= new BaseFilter();
				 if(!f.cleanDoi(doi))
					 return Response.status(400).entity(OmicsServicebean.inputMessageDoi).build();

				datalist=omicsServicebean.findCisConsensomesByDoi(false,true,doi,percentile,OmicsServicebean.MAX_NUMBER_XLS, 0,null);
				query=doi;
			}

			long f=System.currentTimeMillis();
			 File file = consensomeFilesBean.generateCistromicConsensomeExcelData(query,doi,datalist, OmicsServicebean.MAX_NUMBER_XLS);
             if((System.currentTimeMillis()-f) > 10000)
                 log.log(Level.WARNING,"SRX file download taking too long time (ms), consider mongoDb caching="+(System.currentTimeMillis()-f));

             targetStream = new InputStreamWithFileDeletion(file);
		 } catch (IOException e) {
			 log.log(Level.WARNING, "Could not write results to a temp file. Sending error file instead", e);
			 targetStream = context.getResourceAsStream("/WEB-INF/error.xls");
		 }

		 String fileName="srx_consensome_file";
         if(doi!=null)
             fileName=doi.substring(doi.indexOf("/")+1);

		 Response.ResponseBuilder response = Response.ok((Object) targetStream);
		 response.header("Content-Disposition",
				 "attachment; filename=" + fileName + ".xls");
		 return response.build();
	 }
	 
	 public  Response findSummaries(){
		 List<ConsensomeSummary> list=(List<ConsensomeSummary>)omicsServicebean.findConsensomeSummary(QueryType.Cistromic,null,null,null,null,null);
		 return Response.ok().entity(list).build();
	 }
	 
	 public Response findSummary(String family,String ps,String organ,String species,String v) {
	  	if(family == null && species == null)
			return Response.status(400).entity(OmicsServicebean.inputMessage).build();

		Map<String,String> input=this.omicsServicebean.recastFamilyPsOrganOfNull(family,ps,organ);
         family=input.get("family");
         ps=input.get("ps");
         organ=input.get("organ");

	  	List<ConsensomeSummary> list=(List<ConsensomeSummary>)omicsServicebean.findConsensomeSummary(QueryType.Cistromic,null,family,ps,organ,species);

		 return (list.size()>0)?Response.ok().entity(list.get(0)).build():null;
	 }
		
	public Response findSummaryByDoi(String doi) {
	   	 
		if(doi == null )
			return Response.status(400).entity(OmicsServicebean.inputMessageDoi).build();
			
		if(doi.length() >0 ){

			Optional<Response> invalidResp=omicsServicebean.isGotInvalidDOIResponse(doi);
			if(invalidResp.isPresent())
				return invalidResp.get();
		}
		List<ConsensomeSummary> list=(List<ConsensomeSummary>)omicsServicebean.findConsensomeSummary(QueryType.Cistromic,doi,null,null,null,null);
		return (list.size()>0)?Response.ok().entity(list.get(0)).build():null;
	}


	public Response getAntigenBindingScoresCount(String omics, String type, String gene, String doi,String ps,
												 String organ, String tissue, String species, String pathwayType, String pathway,
												 Integer minScore,Integer maxScore) {

		long b=System.currentTimeMillis();
		SimpleQueryForm sform=this.omicsServicebean.formSimpleQueryForm(omics,type, gene,doi,
				ps, organ,tissue,species, pathwayType, pathway,
				minScore,maxScore,null,null);

		String errorMsg=this.omicsServicebean.getSimpleInputError(sform);

		if(errorMsg != null)
			return Response.status(400).entity(errorMsg).build();

		errorMsg=this.omicsServicebean.validatePathwayInput(pathway,pathwayType);
		if(errorMsg!=null)
			return Response.status(400)
					.entity(errorMsg).build();
		if((System.currentTimeMillis()-b) > 100)
			log.log(Level.WARNING,"SRX Count prep slow ="+(System.currentTimeMillis()-b));

		b=System.currentTimeMillis();
		int count=this.omicsServicebean.countDatapoints(sform,QueryType.Cistromic,false,true,0L);
		if((System.currentTimeMillis()-b) > 1000)
			log.log(Level.WARNING,"SRX Count slow #->ms ="+count+"->"+(System.currentTimeMillis()-b));

		return Response.ok().entity(count).build();
	}
		
	@Override
	public Response getAntigenBindingScores(String omics, 
			String geneSearchType, 
			String gene, String doi,
			String ps, String organ,String tissue, 
			String species, 
			String pathwayCategory, String signalingPathway,
			Integer minScore, Integer maxScore,String bsm, String reportBy,Integer countMax) {

	     log.log(Level.INFO," ************SRX query...*************");
	     if(countMax.intValue() > OmicsServicebean.MAX_ABS_NUMBER_DATAPOINTS){
            return Response.status(400).entity("The Service cannot return the requested number of datapoints").build();
         }

        Map map=this.omicsServicebean.prepCisSimpleQueryForm(true,omics,geneSearchType,gene,doi,ps,organ,tissue,species,
                pathwayCategory,signalingPathway,minScore,maxScore,bsm);

        if(map.containsKey("error"))
            return Response.status(400).entity(map.get("error")).build();

        SimpleQueryForm sform=(SimpleQueryForm)map.get("sform");
        QueryForm queryForm=(QueryForm)map.get("qform");

		//log.log(Level.WARNING,"getAntigenBindingScores cache key =>"+sform.cacheKey());

        if(ApisCacheManager.queryCache.containsKey(sform.cacheKey())){
			//log.log(Level.INFO,"SRX cache hit ... new qform id=>"+queryForm.getId());
            this.omicsServicebean.persistQueryId(sform,queryForm);
			//log.log(Level.INFO,"SRX cache hit new qform(file) cache id =>"+queryForm.getId());
            TmQueryResponse<OmicsDatapoint> cacheResult=ApisCacheManager.queryCache.get(sform.cacheKey());
            cacheResult.getQueryForm().setId(queryForm.getId());
            return Response.ok().entity(cacheResult).build();
        }

        Map cmap=this.omicsServicebean.checkCountCriteria(true,false,true,QueryType.Cistromic,sform,queryForm,0L,countMax);
        this.omicsServicebean.persistQueryId(sform,queryForm);

        if(cmap.containsKey("error"))
            return (Response)cmap.get("error");

        int count=(Integer)cmap.get("count");

        long b=System.currentTimeMillis();

		boolean byDatapoints=(reportBy.equalsIgnoreCase("datapoints"));

		List<OmicsDatapoint> datalist=null;
		List<OmicsDatapointReport> rdatalist=null;
		TmQueryResponse<OmicsDatapoint> result=null;

		Integer maxCountBeforeAggregate=countMax; //duplicates of datapoints allowed or SRX
		if(byDatapoints) {
			datalist = (List<OmicsDatapoint>) this.omicsServicebean.query(sform, true,false, true,QueryType.Cistromic, byDatapoints, 0L,maxCountBeforeAggregate);
			result = new TmQueryResponse<OmicsDatapoint>(queryForm, new Long(datalist.size()), datalist);
		}else{
            rdatalist = (List<OmicsDatapointReport>) this.omicsServicebean.query(sform, true, false,true,QueryType.Cistromic, byDatapoints,0L,maxCountBeforeAggregate);
			datalist= new ArrayList<>();
			for(OmicsDatapointReport report:rdatalist){
				datalist.addAll(report.getDatapoints());
			}
			result= new TmQueryResponse<OmicsDatapoint>(new Long(count),queryForm, null,rdatalist);
		}

		//FIXME need to update graph to allow more
		//since poinst can be duplicated, possible have more than count
        if(datalist.size() > OmicsServicebean.MAX_ABS_NUMBER_DATAPOINTS){
            TmQueryResponse<OmicsDatapoint> tooMuch=new TmQueryResponse<OmicsDatapoint>(queryForm, new Long(datalist.size()), null);
            if((System.currentTimeMillis()-b)>3000)
                log.log(Level.WARNING,"SRX query=data over limit is slow #count/#datalist => ms="+count+"/"+datalist.size()+"=>"+(System.currentTimeMillis()-b));
            ApisCacheManager.queryCache.putIfAbsent(sform.cacheKey(),tooMuch);
            return Response.status(413).entity(tooMuch).build();
        }

        if(datalist.size() > countMax.intValue() ){
			TmQueryResponse<OmicsDatapoint> tooMuch=new TmQueryResponse<OmicsDatapoint>(queryForm, new Long(datalist.size()), null);
            if((System.currentTimeMillis()-b)>3000)
                log.log(Level.WARNING,"SRX query=too much data is slow #count/#datalist => ms="+count+"/"+datalist.size()+"=>"+(System.currentTimeMillis()-b));
            ApisCacheManager.queryCache.putIfAbsent(sform.cacheKey(),tooMuch);
            return Response.ok().entity(tooMuch).build();
		}

		if(byDatapoints) {
			result.setResults(datalist);
		}

		if((System.currentTimeMillis()-b)>3000)
			log.log(Level.WARNING,"SRX total query # => ms="+datalist.size()+"=>"+(System.currentTimeMillis()-b));

        //cache response
        ApisCacheManager.queryCache.putIfAbsent(sform.cacheKey(),result);

		return Response.ok().entity(result).build();
	}


    @Override
    public Response getMacsScoreDetails(Long datapointId) {
        if(datapointId == null)
            return Response.status(400).entity("Missing datapointId").build();

        long b=System.currentTimeMillis();
        Optional<OmicsMacsPeakDetail> datapoint=omicsServicebean.findOmicsMacs2PeakDetail(datapointId);
        if((System.currentTimeMillis()-b)> 5000)
            log.log(Level.INFO,"slow.query for MACS2 score ms= "+((System.currentTimeMillis()-b)));

        if(datapoint.isPresent())
            return Response.ok().entity(datapoint.get()).build();
        else
            return Response.status(400).entity("No Cistromic datapoint with that id").build();
    }

    public Response downloadCistromicDataAsExcelFile(String queryId, String fileName, String email) {

	 	InputStream targetStream = null;
	        try {
	        	if(queryId == null || queryId.length() == 0)
	            	return Response.status(404).entity("Please provide an existing queryId to download").build();

	        	Optional<SimpleQueryForm> form=this.omicsServicebean.getSimpleQueryForm(queryId);

	        	if(!form.isPresent()){
					log.log(Level.WARNING, "That file download link expired for queryId=>"+queryId);
					return Response.status(404).entity("That file download link expired. Please re-submit the query to get a new File download link.").build();
				}
				SimpleQueryForm queryForm=form.get();
	        	int max=OmicsServicebean.MAX_NUMBER_XLS;

	        	List<OmicsDatapoint> datalist=(List<OmicsDatapoint>)omicsServicebean.query(queryForm,false,false,true,QueryType.Cistromic,true,0L,max);
	        	File file = fileHelper.generateCistromicQueryExelBook(datalist, queryForm, OmicsServicebean.MAX_NUMBER_XLS);
	            
	            targetStream = new InputStreamWithFileDeletion(file);
	        } catch (IOException e) {
	            log.log(Level.WARNING, "Could not write results to a temp file. Sending error file instead", e);
	            targetStream = context.getResourceAsStream("/WEB-INF/error.xls");
	        }

	        Response.ResponseBuilder response = Response.ok((Object) targetStream);
	        response.header("Content-Disposition",
	                "attachment; filename=" + fileName + ".xls");
	        return response.build();
	    }

	 	@Override
		public Response getTopBindingScoresGenes(String repo, Long experimentid, Integer count) {
				
			 if((repo == null || repo.trim().length() == 0) && (experimentid==null))
					return Response.status(400).entity("Need provide a dataset identifier,repo or id : "+repo).build();
			
			 CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<BindingScore> criteria = cb.createQuery(BindingScore.class);
				Root<BindingScore> scoreRoot = criteria.from(BindingScore.class);
				Root<Experiment> expRoot = criteria.from(Experiment.class);
				Root<NURSADataset> dsetRoot = criteria.from(NURSADataset.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				
				predicates.add(cb.equal(expRoot.get(Experiment_.id), scoreRoot.get(BindingScore_.experiment).get(Experiment_.id)));
				predicates.add(cb.equal(dsetRoot.get(NURSADataset_.id), expRoot.get(Experiment_.dataset).get(NURSADataset_.id)));
				
				predicates.add(cb.greaterThan(scoreRoot.get(BindingScore_.score), CistromicsRestService.MINIMUM_BINDING_SCORE));
				
				if(repo != null)
			 		predicates.add(cb.equal(dsetRoot.get(NURSADataset_.repo), repo));
			 	
				if(experimentid != null && experimentid > 0)
			 		predicates.add(cb.equal(expRoot.get(Experiment_.id), experimentid));
			 	
				criteria.orderBy(cb.desc(scoreRoot.get(BindingScore_.score)));
				criteria.multiselect(scoreRoot.get(BindingScore_.id),
						scoreRoot.get(BindingScore_.gene),
						scoreRoot.get(BindingScore_.score))
				.where(predicates.toArray(new Predicate[]{}));
				
				List<BindingScore> list = em.createQuery(criteria)
						.setMaxResults(count).getResultList();
				
			 return Response.ok().entity(list).build();
		}
}