package edu.bcm.dldcc.big.nursa.services.rest.omics;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import edu.bcm.dldcc.big.nursa.model.omics.dto.OmicsDatapoint;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;

import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;
import edu.bcm.dldcc.big.nursa.model.omics.dto.ConsensomeDataWrap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/omics/cistromics/")
@Api(value = "/omics/cistromics/",tags="Cistromics Dataset and Antigen Binding Scores Datapoints Query(Internal)")
public interface CistromicsRestService   {

	String PATHWAY_NODE_SEPARATOR="::";
	Integer MINIMUM_BINDING_SCORE=0;
	String NODE_SOURCE_IPAGS="ipags";
	String NODE_SOURCE_OAGS="oags";
    String NODE_SOURCE_MODEL="models";

	String SEARCH_TYPE_GENE_LIST = "geneList";
	String SEARCH_TYPE_SINGLE_GENE = "gene";
	String SEARCH_TYPE_SINGLE_GENE_EG = "entrezGeneId";
	String SEARCH_TYPE_DOI = "doi";
	String SEARCH_TYPE_GO_TERM = "goTerm";
	
	int UI_DATA_MAX=2000;
	int XLS_DATA_MAX=20000;

	//cistromics reportsBy datapoints vs Tx is by pathways??
	 @GET
	    @Path("datapoints")
	    @Produces("application/json")
     @Cache(maxAge=43200,sMaxAge=43200) //Must sycn with file download cache time
     @GZIP
	    @ApiOperation(value = "Find antigen binding scores datapoints, for given parameters.  ",
	    notes = "Unsupplied params are defaulted",
	    response = OmicsDatapoint.class, responseContainer = "List")
	 
	    @ApiResponses(value = { 
		@ApiResponse(code = 200, message="Find Cistromics antigen binding scores datapoints, for given parameters with pertinent filters", 
				response=OmicsDatapoint.class, responseContainer = "List"),
		@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	 Response getAntigenBindingScores(
			 @ApiParam(value = "omics(both,Cistromic,Transcriptomic)", required = true) @QueryParam("omics") @DefaultValue("Cistromic") String omics,
			 @ApiParam(value = "geneSearchType (gene,consensome,GoTerm)", required = true) @QueryParam("geneSearchType") String geneSearchType,
			 @ApiParam(value = "gene", required = false) @QueryParam("gene") String gene,
			 @ApiParam(value = "doi", required = false) @QueryParam("doi") String doi,
			 @ApiParam(value = "ps", required = false) @QueryParam("ps") String ps,
			 @ApiParam(value = "organ", required = false) @QueryParam("organ") String organ,
			 @ApiParam(value = "tissue", required = false) @QueryParam("tissue") String tissue,
			 @ApiParam(value = "species", required = false) @QueryParam("species") @DefaultValue("all") String species,
			 @ApiParam(value = "pathwayType", required = false) @QueryParam("pathwayType") String pathwayType,
			 @ApiParam(value = "signalingPathway", required = false) @QueryParam("signalingPathway") String signalingPathway,
			 @ApiParam(value = "minScore", required = false) @QueryParam("minScore") @DefaultValue("0") Integer minScore,
			 @ApiParam(value = "maxScore", required = false) @QueryParam("maxScore") Integer maxScore,
			 @ApiParam(value = "bsm (just to test)", required = false) @QueryParam("bsm") String bsm,
			 @ApiParam(value = "reportsBy", required = false) @QueryParam("reportsBy") @DefaultValue("datapoints") String reportsBy,
			 @ApiParam(value = "countMax", required = false) @QueryParam("countMax") @DefaultValue("2000") Integer countMax
	 );

	@GET
	@Path("datapoints/count")
	@Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
	@ApiOperation(value = "Get countDatapoints antigen binding scores datapoints, for given parameters.  ",
			notes = "Unsupplied params are defaulted",response = Long.class)

	@ApiResponses(value = {
			@ApiResponse(code = 200, message="Find countDatapoints of Cistromics antigen binding scores datapoints, for given parameters with pertinent filters",
					response=Long.class),
			@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	Response getAntigenBindingScoresCount(
			@ApiParam(value = "omics(both,Cistromic,Transcriptomic)", required = true) @QueryParam("omics") @DefaultValue("Cistromic") String omics,
			@ApiParam(value = "geneSearchType (gene,consensome,GoTerm)", required = true) @QueryParam("geneSearchType") String geneSearchType,
			@ApiParam(value = "gene", required = false) @QueryParam("gene") String gene,
			@ApiParam(value = "doi", required = false) @QueryParam("doi") String doi,
			@ApiParam(value = "ps", required = false) @QueryParam("ps") String ps,
			@ApiParam(value = "Organ", required = false) @QueryParam("organ") String organ,
			@ApiParam(value = "tissue", required = false) @QueryParam("tissue") String tissue,
			@ApiParam(value = "species", required = false) @QueryParam("species") @DefaultValue("-1") String species,
			@ApiParam(value = "pathwayType", required = false) @QueryParam("pathwayType") String pathwayType,
			@ApiParam(value = "signalingPathway", required = false) @QueryParam("signalingPathway") String signalingPathway,
			@ApiParam(value = "minScore", required = false) @QueryParam("minScore") @DefaultValue("0") Integer minScore,
			@ApiParam(value = "maxScore", required = false) @QueryParam("maxScore") Integer maxScore
	);

	@GET
	@Path("datapoints/macs2peak/{datapointId}")
	@Consumes("application/json")
	@Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
	@ApiOperation(value = "Get Cistromics datapoint MACS2 Peak Detail",notes = "Supply prior selected datapoint id ")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message="Cistromic datapoint MACS2 Peak Details"),
			@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	Response getMacsScoreDetails(
			@ApiParam(value = "datapointId", required = true) @PathParam("datapointId") Long datapointId);

	 @GET
	    @Path("datapoints/top")
	    @Produces("application/json")
     @Cache(maxAge=86400,sMaxAge=86400)
	    @ApiOperation(value = "Find top genes by antigen binding scores",
	    notes = "Find top genes by antigen binding scores",
	    response = OmicsDatapoint.class, responseContainer = "List")
	 	@ApiResponses(value = { 
		@ApiResponse(code = 200, message="For given Dataset, List to (50) high score genes", 
				response=OmicsDatapoint.class, responseContainer = "List"),
		@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
		@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	 Response getTopBindingScoresGenes(
			 @ApiParam(value = "repo", required = false) @QueryParam("repo") String repo,
			 @ApiParam(value = "experimentid", required = false) @QueryParam("experimentid") Long experimentid,
			 @ApiParam(value = "countDatapoints", required = false) @QueryParam("countDatapoints") @DefaultValue("100") Integer count
	 );
	 
	 @GET
	    @Path("datapoints/download/excel/{queryId}")
	    @Produces("application/vnd.ms-excel")
	    @ApiOperation(value = "Download Cistromic datapoints",notes = "Supply prior queryId")
	    @ApiResponses(value = { 
	    	@ApiResponse(code = 200, message="Download Excel file of Cistomic datapoints"),
	    	@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
	    	@ApiResponse(code = 404, message = "File download link expired", response = String.class),
	    	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
	 Response downloadCistromicDataAsExcelFile(
			 @ApiParam(value = "queryId", required = true) @PathParam("queryId") String queryId,
			 @ApiParam(value = "fileName", required = false) @QueryParam("fileName") @DefaultValue("SRX_results_file") String fileName,
			 @ApiParam(value = "email", required = false) @QueryParam("email") String email);

	 @GET
	    @Path("v1/consensome/summaries")
	    @Produces({ "application/json" })
		 @Cache(maxAge=86400,sMaxAge=86400)
	    @ApiOperation(value = "Show all active/current summaries", notes = "Show all active/current summaries.", 
		 response = ConsensomeSummary.class, responseContainer = "List")
	    @ApiResponses(value = { 
			@ApiResponse(code = 400, message="Incorrect query params", response=String.class),
			@ApiResponse(code = 200, message="Summary Info", response=ConsensomeSummary.class) })
	 Response findSummaries();
	 
	 @GET
	    @Path("v1/consensome/summary")
	    @Produces({ "application/json" })
		 @Cache(maxAge=86400,sMaxAge=86400)
	    @ApiOperation(value = "Show relevant active summary", notes = "Show  active/current summary.", 
		 response = ConsensomeSummary.class)
	    @ApiResponses(value = { 
			@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
			@ApiResponse(code = 200, message="Summary", response=ConsensomeSummary.class) })
	 Response findSummary(
			 @ApiParam(value = "signalingPathway (familyId)", required = false) @QueryParam("signalingPathway") String family,
			 @ApiParam(value = "ps (psId)", required = false) @QueryParam("psId") String ps,
			 @ApiParam(value = "organ (organId)", required = false) @QueryParam("organ") String organ,
			 @ApiParam(value = "species", required = true) @QueryParam("species") String species,
			 @ApiParam(value = "version", required = false) @QueryParam("v") String date);
	 
		@GET
	    @Path("v1/consensome/summary/doi")
	    @Produces({ "application/json" })
		 @Cache(maxAge=86400,sMaxAge=86400)
	    @ApiOperation(value = "Find  active summary by DOI", notes = "Show  active/current summary by DOI.", 
		 response = ConsensomeSummary.class)
	    @ApiResponses(value = { 
			@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
			@ApiResponse(code = 200, message="Summary by DOI", response=ConsensomeSummary.class) })
		Response findSummaryByDoi(
				@ApiParam(value = "doi", required = true) @QueryParam("doi") String doi);
	 
	 @GET
	    @Path("v1/consensome/datalist")
	    @Produces({ "application/json" })
		 @Cache(maxAge=86400,sMaxAge=86400)
     @GZIP
	    @ApiOperation(value = "Show Cistromic Consensome with count of Datapoints", notes = "Show Cistromic Consensome with count of Datapoints.",
		 response = ConsensomeDataWrap.class)
	    @ApiResponses(value = { 
			@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
			@ApiResponse(code = 200, message="Cistromic Consensome data with countDatapoints",
			response=ConsensomeDataWrap.class,responseContainer = "List") })
	 Response findConsensomesWithCount(
			 @ApiParam(value = "doi", required = false) @QueryParam("doi") String doi,
			 @ApiParam(value = "signalingPathway(familyId)", required = false) @QueryParam("signalingPathway") String familyId,
			 @ApiParam(value = "ps(psId)", required = false) @QueryParam("ps") String psId,
			 @ApiParam(value = "organ(organId)", required = false) @QueryParam("organ") String organId,
			 @ApiParam(value = "species", required = true) @QueryParam("species") String species,
			 @ApiParam(value = "percentile", required = false) @QueryParam("percentile") @DefaultValue("90") double percentile,
			 @ApiParam(value = "countDatapoints", required = false) @QueryParam("countDatapoints") Integer count,
			 @ApiParam(value = "page", required = false) @QueryParam("page") Integer page,
			 @ApiParam(value = "v", required = false) @QueryParam("v") String date);

	 @GET
	    @Path("v1/consensome/excel")
	    @Produces("application/vnd.ms-excel")
		@NoCache
		@ApiOperation(value = "Download  active or archived data", notes = "Download active or archived consensome data.", 
		 response = Response.class)
	   @ApiResponses(value = { 
			@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
			@ApiResponse(code = 200, message="File data", response=String.class) })
	 Response downloadCisConsensomeDataAsExcelFile(
			 @ApiParam(value = "signalingPathway(familyId)", required = false) @QueryParam("signalingPathway") String family,
			 @ApiParam(value = "ps(psId)", required = false) @QueryParam("ps") String ps,
			 @ApiParam(value = "organ(organId)", required = false) @QueryParam("organ") String organ,
			 @ApiParam(value = "species", required = true) @QueryParam("species") String species,
			 @ApiParam(value = "v", required = false) @QueryParam("v") String version,
			 @ApiParam(value = "email", required = false) @QueryParam("email") String email);

	/**
	 * For download, do not limit on percentiles (1.10.2019)
	 * @param queryid
	 * @param doi
	 * @param percentile
	 * @return
	 */
	@GET
	@Path("v1/consensome/download")
	@Produces("application/vnd.ms-excel")
	@NoCache
	@ApiOperation(value = "Download  active or archived data by doi/queryId", notes = "Download  active or archived data by doi/queryid",
			response = Response.class)
	@ApiResponses(value = {
			@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
			@ApiResponse(code = 200, message="File data", response=String.class) })
	Response downloadConsensomeFileByDoiOrQueryId(
			@ApiParam(value = "queryid", required = false) @QueryParam("queryid") String queryid,
			@ApiParam(value = "doi", required = false) @QueryParam("doi") String doi,
			@ApiParam(value = "percentile", required = false) @QueryParam("percentile") @DefaultValue("0") double percentile
	);

}
