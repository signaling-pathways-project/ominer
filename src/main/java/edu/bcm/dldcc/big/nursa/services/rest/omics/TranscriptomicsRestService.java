package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;
import edu.bcm.dldcc.big.nursa.model.omics.dto.ConsensomeDataWrap;
import edu.bcm.dldcc.big.nursa.model.omics.dto.OmicsDatapoint;
import edu.bcm.dldcc.big.nursa.model.omics.dto.TmQueryResponse;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointBasicDTO;
import io.swagger.annotations.*;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/omics/transcriptomics/")
@Api(value = "/omics/transcriptomics/",tags="Transcriptomics Query(Internal)")
public interface TranscriptomicsRestService {

    @GET
    @Path("datapoints/")
    @Produces("application/json")
    @Cache(maxAge=43200,sMaxAge=43200) //Must sycn with file download cache time
    @GZIP
    @ApiOperation(value = "Find datapoints, for given parameter. The defaults apply to optional parameters ( e.g. pValue<=0.05)",
            notes = "Supply either single foldChange value or range of foldChange values",
            response = OmicsDatapoint.class, responseContainer = "List")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message="Find datapoints,  with pertinent filters and defaults",
                    response=OmicsDatapoint.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    Response getTranscriptomicDatapoints(
            @ApiParam(value = "omics(both,Cistromic,Transcriptomic)", required = true) @QueryParam("omics") @DefaultValue("Cistromic") String omics,
            @ApiParam(value = "geneSearchType (gene,consensome,GoTerm)", required = true) @QueryParam("geneSearchType") String geneSearchType,
            @ApiParam(value = "gene", required = false) @QueryParam("gene") String gene,
            @ApiParam(value = "bsm", required = false) @QueryParam("bsm") String bsm,
            @ApiParam(value = "doi", required = false) @QueryParam("doi") String doi,
            @ApiParam(value = "ps", required = false) @QueryParam("ps") String ps,
            @ApiParam(value = "organ", required = false) @QueryParam("organ") String organ,
            @ApiParam(value = "tissue", required = false) @QueryParam("tissue") String tissue,
            @ApiParam(value = "species", required = false) @QueryParam("species") @DefaultValue("all") String species,
            @ApiParam(value = "pathwayType", required = false) @QueryParam("pathwayType") String pathwayType,
            @ApiParam(value = "signalingPathway", required = false) @QueryParam("signalingPathway") String signalingPathway,
            @ApiParam(value = "significance", required = false) @QueryParam("significance") @DefaultValue("0.05") String significance,
            @ApiParam(value = "direction", required = false) @QueryParam("direction") @DefaultValue("any") String direction,
            @ApiParam(value = "foldChange", required = false) @QueryParam("foldChange") String foldChange,
            @ApiParam(value = "foldChangeMin", required = false) @QueryParam("foldChangeMin") String foldChangeMin,
            @ApiParam(value = "foldChangeMax", required = false) @QueryParam("foldChangeMax") String foldChangeMax,
            @ApiParam(value = "findMax", required = false) @QueryParam("findMax") @DefaultValue("n") String findMax,
            @ApiParam(value = "reportsBy", required = false) @QueryParam("reportsBy") @DefaultValue("datapoints") String reportsBy,
            @ApiParam(value = "countMax", required = false) @QueryParam("countMax") @DefaultValue("2000") Integer countMax

    );

    @GET
    @Path("datapoints/count")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Find datapoints,  for  with pertinent filters and defaults. The defaults apply to optional parameters  ( e.g. pValue<=0.05)",
            notes = "Supply either single foldChange value or min and max foldChange values",response = Long.class)

    @ApiResponses(value = {
            @ApiResponse(code = 200, message="Count datapoints, for  with pertinent filters and defaults",
                    response=Long.class),
            @ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    Response countTranscriptomicDatapoints(
            @ApiParam(value = "omics(both,Cistromic,Transcriptomic)", required = true) @QueryParam("omics") @DefaultValue("Cistromic") String omics,
            @ApiParam(value = "geneSearchType (gene,consensome,GoTerm)", required = true) @QueryParam("geneSearchType") String geneSearchType,
            @ApiParam(value = "gene", required = false) @QueryParam("gene") String gene,
            @ApiParam(value = "bsm", required = false) @QueryParam("bsm") String bsm,
            @ApiParam(value = "doi", required = false) @QueryParam("doi") String doi,
            @ApiParam(value = "ps", required = false) @QueryParam("ps") String ps,
            @ApiParam(value = "Organ", required = false) @QueryParam("organ") String organ,
            @ApiParam(value = "tissue", required = false) @QueryParam("tissue") String tissue,
            @ApiParam(value = "species", required = false) @QueryParam("species") @DefaultValue("-1") String species,
            @ApiParam(value = "pathwayType", required = false) @QueryParam("pathwayType") String pathwayType,
            @ApiParam(value = "signalingPathway", required = false) @QueryParam("signalingPathway") String signalingPathway,
            @ApiParam(value = "significance", required = false) @QueryParam("significance") @DefaultValue("0.05") String significance,
            @ApiParam(value = "direction", required = false) @QueryParam("direction") @DefaultValue("any") String direction,
            @ApiParam(value = "foldChange", required = false) @QueryParam("foldChange") String foldChange,
            @ApiParam(value = "foldChangeMin", required = false) @QueryParam("foldChangeMin") String foldChangeMin,
            @ApiParam(value = "foldChangeMax", required = false) @QueryParam("foldChangeMax") String foldChangeMax
    );


    @GET
    @Path("datapoints/findDataPointsByExpId")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Find datapoints, for given fold change and experiment id, with the rest as defaults ( eg pValue<=0.05)",
            notes = "Supply either single foldChange Value or min and max foldChange values",
            response = DatapointBasicDTO.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message="Datapoints for the given Experiment with pertinent filters",
                    response=TmQueryResponse.class),
            @ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    Response findBasicDatapointsByExpId(
            @ApiParam(value = "Experiment Id", required = true) @QueryParam("experimentid") Long expId,
            @ApiParam(value = "FoldChangeView", required = false) @QueryParam("foldChange") String fc,
            @ApiParam(value = "FoldChangeMin", required = false) @QueryParam("foldChangeMin") String fcMin,
            @ApiParam(value = "FoldChangeMax", required = false) @QueryParam("foldChangeMax") String fcMax,
            @ApiParam(value = "Direction", required = false) @QueryParam("direction") String direction,
            @ApiParam(value = "AbsoluteSort", required = false) @QueryParam("AbsoluteSort") String absSort,
            @ApiParam(value = "findMax", required = false) @QueryParam("findMax") @DefaultValue("n") String findMax,
            @ApiParam(value = "countMax", required = false) @QueryParam("countMax") @DefaultValue("100") Integer countMax
    );


    /**
     * Gets foldChange detail
     * @param datapointId
     * @return
     */
    @GET
    @Path("foldchange/{datapointId}")
    @Consumes("application/json")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Get FoldChange Detail",notes = "Supply prior selected datapoint id ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message="FoldChange Details"),
            @ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    Response findFoldchangeDetailsByDatapointID(
            @ApiParam(value = "datapointId", required = true) @PathParam("datapointId") Long datapointId);


    @GET
    @Path("download/excel/{queryId}")
    @Produces("application/vnd.ms-excel")
    @ApiOperation(value = "Download TX query datapoints",notes = "Supply prior query id ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message="Download Excel of TX datapoints"),
            @ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
            @ApiResponse(code = 404, message = "File download link expired", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    Response downloadTxExcelFile(
            @ApiParam(value = "queryId", required = true) @PathParam("queryId") String queryId,
            @ApiParam(value = "fileName", required = false) @QueryParam("fileName") @DefaultValue("txomics_query_results_file") String fileName,
            @ApiParam(value = "email", required = false) @QueryParam("email") String email);

    @GET
    @Path("datapoints/fc/max/{queryId}")
    @Produces("application/json")
    @ApiOperation(value = "Largest TX query fold change",notes = "Supply prior query id ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message="Largest TX query fold change"),
            @ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
            @ApiResponse(code = 404, message = "Query id  expired", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    Response getLargestFoldChange(@ApiParam(value = "queryId", required = true) @PathParam("queryId") String queryId);

    @GET
    @Path("v1/consensome/summaries")
    @Produces({ "application/json" })
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Show all active/current Transcriptomic Consensome summaries", notes = "Show all active/current Transcriptomic Consensome  summaries.",
            response = ConsensomeSummary.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message="Incorrect endpoint", response=String.class),
            @ApiResponse(code = 200, message="Summary Info", response=ConsensomeSummary.class) })
    Response findSummaries();

    @GET
    @Path("v1/consensome/summary")
    @Produces({ "application/json" })
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Show relevant active summary", notes = "Show  active/current summary.",
            response = ConsensomeSummary.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message="Incorrect query params", response=String.class),
            @ApiResponse(code = 200, message="Summary", response=ConsensomeSummary.class) })
    Response findSummary(
            @ApiParam(value = "signalingPathway(familyId)", required = false) @QueryParam("signalingPathway") String family,
            @ApiParam(value = "ps(psId)", required = false) @QueryParam("ps") String ps,
            @ApiParam(value = "organ(organId)", required = false) @QueryParam("organ") String organ,
            @ApiParam(value = "species", required = true) @QueryParam("species") String species,
            @ApiParam(value = "version", required = false) @QueryParam("v") String date);

    @GET
    @Path("v1/consensome/summary/doi")
    @Produces({ "application/json" })
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Find  active summary by DOI", notes = "Show  active/current summary by DOI.",
            response = ConsensomeSummary.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message="Incorrect query params", response=String.class),
            @ApiResponse(code = 200, message="Summary by DOI", response=ConsensomeSummary.class) })
    Response findSummaryByDoi(
            @ApiParam(value = "doi", required = true) @QueryParam("doi") String doi);

    @GET
    @Path("v1/consensome/datalist")
    @Produces({ "application/json" })
    @Cache(maxAge=86400,sMaxAge=86400)
    @GZIP
    @ApiOperation(value = "Show Transcriptomic Consensome with a count of Datapoints", notes = "Show Transcriptomic Consensome with a count of Datapoints.",
            response = ConsensomeDataWrap.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message="Incorrect query params", response=String.class),
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
    Response downloadConsensomeExcelFile(
            @ApiParam(value = "signalingPathway(familyId)", required = false) @QueryParam("signalingPathway") String family,
            @ApiParam(value = "ps(psId)", required = false) @QueryParam("ps") String ps,
            @ApiParam(value = "organ(organId)", required = false) @QueryParam("organ") String organ,
            @ApiParam(value = "species", required = true) @QueryParam("species") String species,
            @ApiParam(value = "v", required = false) @QueryParam("v") String version,
            @ApiParam(value = "email", required = false) @QueryParam("email") String email);

    /**
     * 1.10.2019, no percentile limits on donload
     * @param queryid
     * @param doi
     * @param percentile
     * @return
     */
    @GET
    @Path("v1/consensome/download")
    @Produces("application/vnd.ms-excel")
    @NoCache
    @ApiOperation(value = "Download  active or archived data by doi/queryid", notes = "Download  active or archived data by doi/queryid",
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
