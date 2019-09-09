package edu.bcm.dldcc.big.nursa.services.rest.pubapi.legacy;

import edu.bcm.dldcc.big.nursa.model.omics.dto.ApiOmicsDatapoint;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetMinimalDTO;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.ApiErrorMessage;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.DatasetMetadata;
import edu.bcm.dldcc.big.nursa.util.qualifier.ApiSecured;
import io.swagger.annotations.*;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * After refining /2/, these unused alternative
 */
@Path("/api/1b/")
@Api(value = "/api/1b/", tags = "Signalingpathway Project public API (v2) but old defns")
public interface ApiServiceRollBack {

    /**
     * FR dataset/experiment metadata
     * @param apiKey
     * @param doi
     * @param pmid
     * @param bsm
     * @param bsmId
     * @param bsmIdType
     * @param node
     * @param biosample
     * @param addedSince
     * @return
     */
    @ApiSecured
    @GET
    @GZIP
    @Path("3/datasets")
    @Produces("application/json")
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "EXPERIMENTAL. Query Transcriptomic and Cistromic Datasets and  experiments metadata",
            notes = "Returns upto 10000 dataset metadata results. Embedded within each dataset metadata are experiment metadata.",
            response = DatasetMetadata.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of queried datasets", response = DatasetMinimalDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = ApiErrorMessage.class)
    })
    public Response findNuDatasets(
            @ApiParam(value = "apiKey", required = true) @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "doi") @QueryParam("doi") String doi,
            @ApiParam(value = "pmid") @QueryParam("pmid") String pmid,
            @ApiParam(value = "bsm (check dictionary for valid SPP's BSM names, e.g. see service endpoint /api/2/bsms/ )") @QueryParam("bsm") String bsm,
            @ApiParam(value = "bsmId (public Ids, e.g. cas,pubchem,iuphar,chebi)") @QueryParam("bsmId") String bsmId,
            @ApiParam(value = "bsmIdType (type required if using bsmId, ie a public Id, one of: cas,pubchem,iuphar,chebi)") @QueryParam("bsmIdType") String bsmIdType,
            @ApiParam(value = "node (HGNC symbol name)") @QueryParam("node") String node,
            @ApiParam(value = "biosample (Provide as PhysiologicalSystem/Organ combination. E.g. metabolic/liver. Check dictionary for valid SPP biosample names, eg see service endpoint /api/2/biosamples )") @QueryParam("biosample") String biosample,
            @ApiParam(value = "addedSince ( Date when dataset was added to SPP, as yyyyMMdd format, i.e. ISO 8601 without time and zone)") @QueryParam("addedsince") String addedSince,
            @ApiParam(value = "count (The number of records to return)") @QueryParam("count") @DefaultValue("5000") Integer count
    );


    /**
     * FR: id,macs2/fc/pvalue,biosample,type,category,class,family,node
     * Return is really an OmicsDatapoint but the smaller version, like ApiOmicsDatapoint
     *  Using seek pagination for paged data.
     *      * To reduce paging complexities, datapoints are sorted by datapointid (so no custom sort field)
     *      * Caller sends starting id (ie highest id of last datapoint) to page on
     *      *
     * @param apiKey
     * @param omicsType
     * @param queryType
     * @param queryValue
     * @param species
     * @param sigPathNodeType
     * @param sigPathNodeId
     * @param ps
     * @param organ
     * @param afterId
     * @param minScore
     * @param maxScore
     * @param foldChange
     * @param foldChangeMin
     * @param foldChangeMax
     * @param significance
     * @param direction
     * @param countMax
     * @return
     */
    @ApiSecured
    @GET
    @GZIP
    @Path("3/datapoints")
    @Produces("application/json")
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "EXPERIMENTAL. Query Transcriptomic/Cistromic Datapoints. Returns upto 10000 datapoints.",
            notes = "Since endpoint is for both Transcriptomic and Cistromic datapoints, optional(filtering) parameter applicability depend on the value of the 'omicsType' parameter. " +
                    "Reasonable filter defaults apply, if ommitted. E.g. for Transcriptomic datapoints,  FoldChange: (>=2 or <=0.5), Significance (<=0.05) and Direction (any). Add relevant filters as needed. " +
                    "Note:  Instead of getting a large data payload, it is far better to do paging by utilizing the startId parameter (the data is sorted ASC by the id field). " +
                    "Also, the number of datapoints returned may be higher than the specified countMax parameter, since multiple family tree hierarchies may map to the same datapoint. ",
            response = ApiOmicsDatapoint.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of Datapoints", response = ApiOmicsDatapoint.class, responseContainer = "List"),
            @ApiResponse(code = 413, message = "Payload result will be too large", response = ApiErrorMessage.class),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = ApiErrorMessage.class)
    })
    public Response findDatapoints(
            @ApiParam(value = "apiKey", required = true) @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "omicsType (Transcriptomic,Cistromic)", required=true) @QueryParam("omicsType") String omicsType,
            @ApiParam(value = "queryType (entrezGeneId,GoTerm,doi,biosample)", required = true) @QueryParam("queryType") String queryType,
            @ApiParam(value = "queryValue (The value to search on, e.g. dataset doi. For biosample, provide as PhysiologicalSystem/Organ combination, e.g. queryValue=metabolic/liver.)", required = true) @QueryParam("queryValue") String queryValue,
            @ApiParam(value = "species (Human,Mouse,Rat)", required = false)  @QueryParam("species")  String species,
            @ApiParam(value = "sigPathNodeHirchyType(domain,category,class,family)", required = false)  @QueryParam("sigPathNodeHirchyType") String sigPathNodeType,
            @ApiParam(value = "sigPathNodeId (see Id dictionary, eg see endpoint /api/2/pathwayNodeHierarchy/all )", required = false)  @QueryParam("sigPathNodeId") Integer sigPathNodeId,
            @ApiParam(value = "ps (see dictionary, e.g. endpoint /api/2/biosamples )", required = false)  @QueryParam("ps") String ps,
            @ApiParam(value = "organ (see dictionary, e.g. endpoint /api/2/biosamples)", required = false)  @QueryParam("organ") String organ,
            @ApiParam(value = "startId (for multiple pages, data is sorted by id field, so provide your last highest id for the next page)", required = false) @QueryParam("startId") @DefaultValue("0")  Long afterId,
            @ApiParam(value = "minScore (Cistromic only)", required = false) @QueryParam("minScore") @DefaultValue("0") Integer minScore,
            @ApiParam(value = "maxScore (Cistromic only)", required = false) @QueryParam("maxScore") Integer maxScore,
            @ApiParam(value = "foldChange (Transcriptomic only)", required = false)  @QueryParam("foldChange") String foldChange,
            @ApiParam(value = "foldChangeMin (Transcriptomic only)", required = false)  @QueryParam("foldChangeMin") String foldChangeMin,
            @ApiParam(value = "foldChangeMax (Transcriptomic only)", required = false)  @QueryParam("foldChangeMax") String foldChangeMax,
            @ApiParam(value = "significance (Transcriptomic only)", required = false)  @QueryParam("significance") @DefaultValue("0.05")  String significance,
            @ApiParam(value = "direction (Transcriptomic only)", required = false)  @QueryParam("direction") @DefaultValue("any")  String direction,
            @ApiParam(value = "isCount (If seeking the number of datapoints only and not data)", required = false)  @QueryParam("isCount") @DefaultValue("0") Integer isCount,
            @ApiParam(value = "countMax ( Use smaller number for performance. This is an estimate only, as the endpoint may return a number higher than countMax, see endpoint description above.)", required = false) @QueryParam("countMax") @DefaultValue("10000") Integer countMax
    );
}
