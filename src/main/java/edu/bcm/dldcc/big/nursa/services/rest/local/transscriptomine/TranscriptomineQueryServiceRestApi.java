package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParamName;
import edu.bcm.dldcc.big.nursa.model.omics.QueryParameter;
import edu.bcm.dldcc.big.nursa.model.omics.dto.QueryParametersData;
import edu.bcm.dldcc.big.nursa.model.omics.dto.TmQueryResponse;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointDTO;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import edu.bcm.dldcc.big.nursa.services.rest.omics.cache.FileDownloadMemoryCache;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.current.APIService;
import edu.bcm.dldcc.big.nursa.services.utils.FileHelper;
import edu.bcm.dldcc.big.nursa.services.utils.InputStreamWithFileDeletion;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * TM Query Bean
 * @author mcowiti
 *
 */
public class TranscriptomineQueryServiceRestApi implements TranscriptomineQueryService{

    private static Logger log = Logger.getLogger(TranscriptomineQueryServiceRestApi.class.getName());

    @Inject
    private TranscriptomineService omineService;

    @Inject 
	private APIService aPIServiceRestApi; 
	
    @Inject
	private FileHelper fileHelper;
	
    
    @Context
    ServletContext context;
    
   
	public Response findDataPoints(QueryParametersData queryParametersData) {
		String inValidMsg=inValidRequest(queryParametersData);
		if(inValidMsg!=null)
			return Response.status(400).entity(inValidMsg).build();
		
		QueryForm queryForm;
		try {
			queryForm = formatQueryForm(queryParametersData);
		} catch (Exception e) {
			return Response.status(400).entity("Input unwrapping Issues").build();
		}
		
		if(queryParametersData.getFindCount() != null && queryParametersData.getFindCount().length()>0){
			Response countResponse=determineRecordCount(queryForm,queryParametersData.getFindCount());
			if(countResponse !=null)
				return countResponse;
		}
		
		List<DatapointDTO> datapoints= this.omineService.findDataPoints(queryForm, DatapointDTO.class,
				omineService.MAX_NUMBER_DATAPOINTS,true);
		
		datapoints=filterMissingPathways(datapoints);
		
		log.log(Level.FINE,"@findDataPoints post filter #="+datapoints.size());
		return Response.ok().entity(datapoints).build();
    }
	
	/**
	 * //8.15.2017 new bug/issue: data issue where no pathways
	 * @param datapoints
	 * @return
	 */
	private List<DatapointDTO> filterMissingPathways(Collection<DatapointDTO> datapoints){
		List<DatapointDTO> fdatapoints=datapoints.stream()
        .filter(dat -> !dat.getMoleculePathway().equals(TranscriptomineQueryService.PATHWAY_NODE_SEPARATOR))    
        .collect(Collectors.toList()); 
		return fdatapoints;
	}
	
	
	public Response findDataPointsAsTmQueryResponse(QueryParametersData queryParametersData)
    {
		String inValidMsg=inValidRequest(queryParametersData);
		if(inValidMsg!=null)
			return Response.status(400).entity(inValidMsg).build();
		
		QueryForm queryForm=setupQueryParams(queryParametersData);
		queryForm.setQueryParameter(queryParametersData);
		
		TmQueryResponse<DatapointDTO> result= omineService.findDatapointSearchResults(queryForm);
		
		log.log(Level.FINE,"@findDataPointsAsTmQueryResponse #="+result.getResults().size());
		
		List<DatapointDTO> fresults=filterMissingPathways(result.getResults());
		result.setResults(fresults);
		result.setCount(new Long(fresults.size()));
		log.log(Level.FINE,"@findDataPointsAsTmQueryResponse post filter #="+fresults.size());
		
		return Response.ok().entity(result).build();
	}
	
	public Response getDatapoints(String geneSearchType, 
			String gene, String geneList, String molecule,
			String moleculetreatmentTime, 
			String foldChange, String foldChangeMax, String foldChangeMin,
			String significance, String direction, 
			String ps,String organ,String tissue, 
			String disease, String goTerm,
			String species, String signalingPathway,String pathwayCategory,
			String findCount) {

		String tissueCategory=(ps!=null)?ps:organ;
		QueryParametersData queryParametersData=omineService.buildQueryParametersData(
				geneSearchType,gene,geneList,molecule,null,moleculetreatmentTime,
				foldChange, foldChangeMax,foldChangeMin,
				significance, direction,
				tissueCategory,ps,organ,tissue,
				disease, goTerm,species, signalingPathway,pathwayCategory,findCount);
		
		String inValidMsg=inValidRequest(queryParametersData);
		if(inValidMsg!=null)
			return Response.status(400).entity(inValidMsg).build();
		
		QueryForm queryForm;
		try {
			queryForm = formatQueryForm(queryParametersData);
		} catch (Exception e) {
			return Response.status(400).entity("Input unwrapping Issues").build();
		}
		
		if(queryParametersData.getFindCount() != null && queryParametersData.getFindCount().length()>0){
			Response countResponse=determineRecordCount(queryForm,queryParametersData.getFindCount());
			if(countResponse !=null)
				return countResponse;
		}
		
		log.log(Level.INFO, "@getDatapoints ...");
		List<DatapointDTO> datapoints= omineService.findDataPoints(queryForm, DatapointDTO.class,
				omineService.MAX_NUMBER_DATAPOINTS,true);
		return Response.ok().entity(datapoints).build();
    }
	
	private Response determineRecordCount(QueryForm queryForm,String mode ){
		
		int counting=0;
		try{
			if((counting=Integer.parseInt(mode)) == 1)
				return Response.ok().entity(this.omineService.findDatapointsCount(queryForm)).build();
			else{
				if(counting >1)
					return Response.status(400).entity("If you need the Count, set parameter findCount=1").build();
			}
		}catch (Exception e){
			return Response.status(400).entity("Query Input unwrapping Issues").build();
		}
		return null;
	}

	private String inValidRequest(QueryParametersData queryParametersData){
		
		if(queryParametersData == null)
			 return "Bad input. Request parameters needed";
		
		if(queryParametersData.getGeneSearchType() == null)
			return "Bad input. Missing GeneSearchType";
		
		return null;
	}
	
	public Response getLargestFoldChangeInQuery(QueryParametersData queryParametersData){
		String inValidMsg=inValidRequest(queryParametersData);
		if(inValidMsg!=null)
			return Response.status(400).entity(inValidMsg).build();
		QueryForm queryForm;
		try {
			queryForm = formatQueryForm(queryParametersData);
		} catch (Exception e) {
			return Response.status(400).entity("Input unwrapping Issues").build();
		}
		
		Double fc= omineService.findLargestFoldChange(queryForm, DatapointDTO.class);
		return Response.ok().entity(fc).build();
	}
	
	

private QueryForm formatQueryForm(QueryParametersData queryParametersData) throws Exception{
		
		QueryForm queryForm=setupQueryParams(queryParametersData);
		queryForm.setQueryParameter(queryParametersData);
		
		Map<QueryParamName, QueryParameter> query=queryForm.getQueryParameters();
		
		this.omineService.translateParams(query,null,null,
				queryParametersData.getGeneSearchType(), 
				queryParametersData.getGene(),
				queryParametersData.getDisease(), 
				queryParametersData.getGoTerm(), 
				queryParametersData.getBsm(),
				null,queryParametersData.getSignificance(),
				queryParametersData.getFoldChange(),
				queryParametersData.getDirection(),
				queryParametersData.getSpecies(),
				null,null,null,queryParametersData.getMoleculetreatmentTime(),
				queryParametersData.getPs(),queryParametersData.getOrgan(),
				queryParametersData.getTissue(),
				queryParametersData.getFoldChangeMin(),queryParametersData.getFoldChangeMax(),
				queryParametersData.getSignalingPathway(),
				queryParametersData.getPathwayCategory());
		
		
		return queryForm;
	}
	
    public <T> T findBasicDatapointsByExpId(int expId, Double fc,Double fcMin,Double fcMax,String direction,String absSort) {
        return omineService.findBasicDatapointsByExpId(expId, fc,fcMin,fcMax,direction,absSort);
    }
    
    @Inject private FileDownloadMemoryCache fileDownloadMemoryCache;

	@Override
	public Response downloadExcelFile(String queryId, String fileName, String email) {
        InputStream targetStream = null;
        try {
            
        	QueryForm queryForm=fileDownloadMemoryCache.getQueryForm(queryId);
        	if(queryForm == null){
        		return Response.status(404).entity("File download link expired. Please re-submit query to get a new File download link.").build();
        	}

        	//temp
			File file=new File("");
            /*FIXME or going away
            File file = fileHelper.generateTranscriptomicQueryExelBook(omineService.<FoldChangeXML>findDataPoints(queryForm, FoldChangeXML.class,
            		omineService.MAX_NUMBER_XLS,false), 
            		queryForm, omineService.MAX_NUMBER_XLS);
            targetStream = new InputStreamWithFileDeletion(file);

            */
        } catch (Exception e) {
            log.log(Level.WARNING, "Cannot write results to the temp file. Will send the error file", e);
            targetStream = context.getResourceAsStream("/WEB-INF/error.xls");
        }

        Response.ResponseBuilder response = Response.ok((Object) targetStream);
        response.header("Content-Disposition",
                "attachment; filename=" + fileName + ".xls");
        return response.build();
    }

    public Response downloadFileDump(Integer id)
    {
        File f  = new File(String.format("/mnt/nursa-datasets/%d.xlsx", id));

        if (f.exists() && !f.isDirectory())
        {
            Response.ResponseBuilder response = Response.ok((Object) f);
            response.header("Content-Disposition", "attachment; filename=dataset_dump.xls");
            return response.build();
        }
        else
        {
            log.warning(String.format("Failed to download datapoint dump with id %d", id));
            Response.ResponseBuilder response = Response.ok((Object) context.getResourceAsStream("/WEB-INF/file_not_found.xls"));
            response.header("Content-Disposition", "attachment; filename=dataset_dump.xls");
            return response.build();
        }
    }

	@Override
	public Response downloadExcelFileToEmail(String queryId, String fileName, String email) {
		// TODO change dum implementation to actual method
        InputStream file= context.getResourceAsStream("/WEB-INF/sample.xls");

        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename="+"fileName"+".xls");
        return response.build();
	}

	/**
	 * FIXME This Query for dataset is broken APollo 10.9.2019
	 * @param datasetId
	 * @return
	 */
    @Override
    public Response downloadDatasetAsEndNote(String datasetId) {
        InputStream targetStream = context.getResourceAsStream("/WEB-INF/citation.ris");

        try {
            NURSADataset dset = omineService.findDataSetById(Long.parseLong(datasetId));
            if (null != dset) {
                File f = fileHelper.generateRisFile(dset);
                targetStream = new InputStreamWithFileDeletion(f);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Cannot write results dataset to the temp file. Will send the error file", e);
            targetStream = context.getResourceAsStream("/WEB-INF/citation.ris");
        }

        Response.ResponseBuilder response = Response.ok((Object) targetStream);
        response.header("Content-Disposition", "attachment; filename="+"citation"+".ris");
        return response.build();
    }

	@Override
	public <T> T findFoldchangeDetailsByDatapointID(Integer datapointId) {
		return omineService.getFoldchangeDetails(datapointId);
	}

	
	@Override
	public <T> T queryDatasets(Integer geneId, String symbol, 
			String ligandSource, String ligandSourceId) {
		return (T) aPIServiceRestApi.queryDatasets(geneId, symbol, ligandSource, ligandSourceId);
	}
	
	private QueryForm setupQueryParams(QueryParametersData queryParametersData){
		
		QueryForm queryForm= new QueryForm();
		
		Map<QueryParamName,QueryParameter> params=queryForm.getQueryParameters();
		
		params.put(QueryParamName.geneSearchType, 
				getQueryParameter(queryForm,QueryParamName.geneSearchType,queryParametersData.getGeneSearchType()));
		
		if(queryParametersData.getGene()!=null){
			params.put(QueryParamName.gene, 
					getQueryParameter(queryForm,QueryParamName.gene,queryParametersData.getGene()));
		}
		if(queryParametersData.getGeneList()!=null){
			params.put(QueryParamName.geneList, 
					getQueryParameter(queryForm,QueryParamName.geneList,queryParametersData.getGeneList()));
		}
		
		if(queryParametersData.getFoldChange()!=null){
			params.put(QueryParamName.foldChange, 
					getQueryParameter(queryForm,QueryParamName.foldChange,
							queryParametersData.getFoldChange()));
		}
		if(queryParametersData.getFoldChangeMin()!=null){
			params.put(QueryParamName.foldChangeMin, 
					getQueryParameter(queryForm,QueryParamName.foldChangeMin,
							queryParametersData.getFoldChangeMin()));
		}
		if(queryParametersData.getFoldChangeMax()!=null){
			params.put(QueryParamName.foldChangeMax, 
					getQueryParameter(queryForm,QueryParamName.foldChangeMax,
							queryParametersData.getFoldChangeMax()));
		}
		
		if(queryParametersData.getDirection()!=null){
			params.put(QueryParamName.direction, 
					getQueryParameter(queryForm,QueryParamName.direction,queryParametersData.getDirection()));
		}
		
		if(queryParametersData.getSignificance()!=null){
			params.put(QueryParamName.significance, 
					getQueryParameter(queryForm,QueryParamName.significance,queryParametersData.getSignificance()));
		}
		
		if(queryParametersData.getBsm()!=null){
			params.put(QueryParamName.molecule, 
					getQueryParameter(queryForm,QueryParamName.molecule,queryParametersData.getBsm()));
		}
		if(queryParametersData.getMoleculetreatmentTime()!=null){
			params.put(QueryParamName.moleculetreatmentTime, 
					getQueryParameter(queryForm,QueryParamName.moleculetreatmentTime,queryParametersData.getMoleculetreatmentTime()));
		}
		
		if(queryParametersData.getPs()!=null){
			params.put(QueryParamName.ps, 
					getQueryParameter(queryForm,QueryParamName.ps,
							queryParametersData.getPs()));
		}
		if(queryParametersData.getOrgan()!=null){
			params.put(QueryParamName.organ, 
					getQueryParameter(queryForm,QueryParamName.organ,
							queryParametersData.getOrgan()));
		}
		if(queryParametersData.getTissue()!=null){
			params.put(QueryParamName.tissue, 
					getQueryParameter(queryForm,QueryParamName.tissue,
							queryParametersData.getTissue()));
		}
		
		/*if(queryParametersData.getTissueCategory()!=null){
			params.put(QueryParamName.tissueCategory, 
					getQueryParameter(queryForm,QueryParamName.tissueCategory,
							queryParametersData.getTissueCategory()));
		}*/
		
		if(queryParametersData.getSpecies()!=null){
			params.put(QueryParamName.species, 
					getQueryParameter(queryForm,QueryParamName.species,queryParametersData.getSpecies()));
		}
		
		if(queryParametersData.getSignalingPathway()!=null){
			params.put(QueryParamName.signalingPathway, 
					getQueryParameter(queryForm,QueryParamName.signalingPathway,queryParametersData.getSignalingPathway()));
		}
		
		if(queryParametersData.getDisease()!=null){
			params.put(QueryParamName.disease, 
					getQueryParameter(queryForm,QueryParamName.disease,queryParametersData.getDisease()));
		}
		
		if(queryParametersData.getGoTerm()!=null){
			params.put(QueryParamName.goTerm, 
					getQueryParameter(queryForm,QueryParamName.goTerm,queryParametersData.getGoTerm()));
		}
		
		if(queryParametersData.getPathwayType()!=null){
			params.put(QueryParamName.pathwayType, 
					getQueryParameter(queryForm,QueryParamName.pathwayType,queryParametersData.getPathwayType()));
		}
		
		return queryForm;
	}
	
	private QueryParameter getQueryParameter(QueryForm queryForm,QueryParamName type,String value){
		ArrayList<String> values=new ArrayList<String>();
		values.add(value);
		String geneList=null;
		if(type == QueryParamName.geneList)
			geneList=value;
		
		return new QueryParameter(queryForm,type,new HashSet<String>(values),geneList);
	}
}
