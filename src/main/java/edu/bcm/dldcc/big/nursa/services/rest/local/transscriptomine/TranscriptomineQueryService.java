package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.QueryParametersData;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.TmQueryResponse;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointDTO;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.Cache;


/**
 * The TM WS Business interface the TM Query WS.
 * Queries datasets, experiments and datapoints 
 * @author amcowiti
 *
 */
@Path("/transcriptomine/")
//@Api(value = "/transcriptomine/",tags="Transcriptomine Query(Internal)-Deprecated")
public interface TranscriptomineQueryService {

	public final static String PATHWAY_NODE_SEPARATOR="::";
	
    @GET
    @Path("datapoints/findDataPointsByExpId")
    @Produces("application/json")
    @Cache(maxAge=1800,sMaxAge=1800)
    /*@ApiOperation(value = "Find datapoints, for given fold change and experiment id, with the rest as defaults ( eg pValue<=0.05)",
            notes = "Supply either single foldChange Value or min and max foldChange values",
            response = DatapointBasicDTO.class,
            responseContainer = "List")*/
     @ApiResponses(value = { 
			@ApiResponse(code = 200, message="Datapoints for the given Experiment with pertinent filters", 
					response=TmQueryResponse.class),
			@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
     public <T> T findBasicDatapointsByExpId(
    		@ApiParam(value = "Experiment Id", required = true) @QueryParam("experiment_id") int expId,
    		@ApiParam(value = "FoldChangeView", required = false) @QueryParam("foldChange") Double fc,
    		@ApiParam(value = "FoldChangeMin", required = false) @QueryParam("foldChangeMin") Double fcMin,
    		@ApiParam(value = "FoldChangeMax", required = false) @QueryParam("foldChangeMax") Double fcMax,
    		@ApiParam(value = "Direction", required = false) @QueryParam("direction") String direction,
    		@ApiParam(value = "AbsoluteSort", required = false) @QueryParam("AbsoluteSort") String absSort);

    
    /**
     * Find Data points by posting QueryParametersData ( or QueryForm )
     * @param <T>
     * @param queryForm
     * @return
     */
    @POST
    @Path("datapoints/query/")
    @Consumes("application/json")
    @Produces("application/json")
   /*@ApiOperation(value = "Find datapoints, with pValue<=0.05,and foldchange for specific experiment",
            notes = "Multiple value list  can be provided with comma seperated strings",
            response = DatapointDTO.class,
            responseContainer = "List")*/
    public Response findDataPoints(
    		@ApiParam(value = "Query object", required = true) 
    		QueryParametersData queryForm);
    
    
    @GET
    @Path("datapoints/")
    @Produces("application/json")
    @Cache(maxAge=1800,sMaxAge=1800)
   /* @ApiOperation(value = "Find datapoints, for given fold change and parameters. Unsupplied params are defaulted ( eg pValue<=0.05)",
    notes = "Supply either single foldChange value or min and max foldChange values",
    response = DatapointDTO.class, responseContainer = "List")
    */
    @ApiResponses(value = { 
	@ApiResponse(code = 200, message="Find datapoints, for given fold change and parameters with pertinent filters", 
			response=DatapointDTO.class, responseContainer = "List"),
	@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response getDatapoints(
    		@ApiParam(value = "autosuggest", required = true)  @QueryParam("geneSearchType") String geneSearchType, 
    		@ApiParam(value = "gene", required = false)  @QueryParam("gene") String gene, 
    		@ApiParam(value = "geneList", required = false)  @QueryParam("geneList") String geneList, 
    		@ApiParam(value = "molecule", required = false)  @QueryParam("molecule") String molecule,
    		@ApiParam(value = "moleculetreatmentTime", required = false)  @QueryParam("moleculetreatmentTime") String moleculetreatmentTime, 
    		@ApiParam(value = "foldChange", required = false)  @QueryParam("foldChange") String foldChange, 
    		@ApiParam(value = "foldChangeMax", required = false)  @QueryParam("foldChangeMax") String foldChangeMax, 
    		@ApiParam(value = "foldChangeMin", required = false)  @QueryParam("foldChangeMin") String foldChangeMin,
    		@ApiParam(value = "significance", required = false)  @QueryParam("significance") @DefaultValue("0.05")  String significance, 
    		@ApiParam(value = "direction", required = false)  @QueryParam("direction") @DefaultValue("any")  String direction, 
    		
    		@ApiParam(value = "Physiological System", required = false)  @QueryParam("ps") String ps, 
    		@ApiParam(value = "Organ", required = false)  @QueryParam("organ") String organ, 
    		@ApiParam(value = "tissue", required = false)  @QueryParam("tissue") String tissue, 
    		@ApiParam(value = "disease", required = false)  @QueryParam("disease") String disease, 
    		@ApiParam(value = "goTerm", required = false)  @QueryParam("goTerm") String goTerm,
    		@ApiParam(value = "species", required = false)  @QueryParam("species") @DefaultValue("all")  String species, 
    		@ApiParam(value = "signalingPathway", required = false)  @QueryParam("signalingPathway") String signalingPathway,
    		@ApiParam(value = "pathwayCategory", required = false)  @QueryParam("pathwayCategory") String pathwayCategory,
    		@ApiParam(value = "findCount", required = false)  @QueryParam("findCount") String findCount);
    
    
    
    /**
     * Find Data points as TmQueryResponse wrapped in Response by QueryForm
     * The Response includes the original TmQueryForm
      * @param queryForm
     * @return
     */
    @POST
    @Path("datapoint/query/")
    @Consumes("application/json")
    @Produces("application/json")
    /*@ApiOperation(value = "Find datapoints as TmQueryResponse",
    notes = "Multiple value list  can be provided with comma seperated strings",
    response = TmQueryResponse.class)*/
    @ApiResponses(value = { 
        	@ApiResponse(code = 200, message="List datapoints"),
        	@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
        	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response findDataPointsAsTmQueryResponse(QueryParametersData queryForm);

    public Response getLargestFoldChangeInQuery(@ApiParam(value = "Query object", required = true) 
	QueryParametersData queryForm);

    
    /**
     * Gets foldChange detail
     * @param datapointId
     * @return
     */
    @GET
    @Path("foldchange/{datapointId}")
    @Consumes("application/json")
    @Produces("application/json")
    @Cache(maxAge=1800,sMaxAge=1800)
    public <T> T findFoldchangeDetailsByDatapointID(@PathParam("datapointId") Integer datapointId);

    
    /**
     * Download query results.
     * TODO WD change to use query params like in the method {@link #getDatapoints(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)}
     * @param queryId
     * @param fileName
     * @return
     */
    @GET
    @Path("download/excel/{queryId}")
    @Produces("application/vnd.ms-excel")
    //@ApiOperation(value = "Download datapoints",notes = "Supply query params")
    @ApiResponses(value = { 
    	@ApiResponse(code = 200, message="Download Excel of datapoints"),
    	@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
    	@ApiResponse(code = 404, message = "File download link expired", response = String.class),
    	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response downloadExcelFile(
    		@ApiParam(value = "queryId", required = true)  @PathParam("queryId") String queryId,
    		@ApiParam(value = "fileName", required = false)  @QueryParam("fileName") @DefaultValue("tm_results_file") String fileName,
    		@ApiParam(value = "email", required = false)  @QueryParam("email") String email);

    
    @GET
    @Path("FileManager/getDatasetDump")
    @Produces("application/vnd.ms-excel")
    public Response downloadFileDump(@QueryParam("id") Integer id);

    /**
     * Sends the results directly to an email address.
     * Possible use cases
     * (i) User requests email of download
     * (ii) "Giant" results are allowed and the results are too large for immediate download
     *
     * @param queryId
     * @param fileName
     * @param email    required
     * @return
     */
    @GET
    @Path("download/email/excel/{queryId}")
    @Produces("application/json")
    public Response downloadExcelFileToEmail(@PathParam("queryId") String queryId,
                                             @QueryParam("fileName") @DefaultValue("tm_results_file") String fileName,
                                             @QueryParam("email") String email);


    /**
     * Download dataset in EndNote format
     * @param datasetId
     * @return
     */
    @GET
    @Path("download/dataset/{datasetId}")
    @Produces("application/x-research-info-systems")
    public Response downloadDatasetAsEndNote(@PathParam("datasetId") String datasetId);
    
    
    //See also in /api/datasets service, 
    @GET
    @Path("datasets")
    @Produces("application/json")
    @Cache(maxAge=1800,sMaxAge=1800)
    public <T> T queryDatasets(
    		@QueryParam("genes.entrezgeneID") Integer geneId,
            @QueryParam("genes.symbol") String symbol,
            @QueryParam("ligand.source") String ligandSource,
            @QueryParam("ligand.sourceID") String ligandSourceId);
    
	}
