package edu.bcm.dldcc.big.nursa.services.rest.pubapi.current;

import java.util.List;

import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.*;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.ApiErrorMessage;
import edu.bcm.dldcc.big.nursa.services.rest.pubapi.dto.DatasetMetadata;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;

import edu.bcm.dldcc.big.nursa.util.qualifier.ApiSecured;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * TM Public API service
 * Caller must:
 * 1) Have APIKey
 * 2) Obey calls contract, e.g. call frequency limits
 * @author mcowiti
 *
 */
@Path("/api/")
@Api(value = "/api/", tags = "Signalingpathway Project public API (v2)")
public interface APIService {

    public static final String PARAMS_SPLITTER = ";";
    public static final String CAS = "";

    /**
     * Reactome API only requires addedSince since
     * @param apiKey
     * @param doi
     * @param addedSince
     * @param count
     * @return
     */
    @ApiSecured
    @GET
    @GZIP
    @Path("2/datasets")
    @Produces("application/json")
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "Query Transcriptomic and Cistromic Datasets and experiments metadata",
            notes = "Returns upto 1000 dataset metadata (id,doi,name,description,releaseDate, experiment metadata) results. " +
                    "Embedded within each dataset metadata are experiment metadata (id,name,description, species). " +
                    "The endpoint also supports getting dataset metadata by doi, which returns a single record. If both doi and addedsince are present, doi takes precedence. " +
                    "Additional dataset metadata (pubmed) may be requested by setting the defired attributes  in the optional CSV fields parameter",
            response = DatasetMetadata.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of queried datasets", response = DatasetMetadata.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = ApiErrorMessage.class)
    })
    public Response findDatasetsForReactome(
            @ApiParam(value = "apiKey", required = true) @Size(max=36) @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "addedsince ( Date when dataset was added to SPP, as yyyyMMdd format, i.e. ISO 8601 without time and zone)",required = true)  @Size(max=8) @QueryParam("addedsince") String addedSince,
            @ApiParam(value = "doi (Optional, for a single record") @Size(max=20) @QueryParam("doi") String doi,
            @ApiParam(value = "count (The number of records to return)") @QueryParam("count") @DefaultValue("1000") Integer count,
            @ApiParam(value = "fields (A CSV of extra (beyond defaults) fields attributes to include return object)") @QueryParam("fields")  String fields
    );

    /**
     * Reactome specific query
     * @param apiKey
     * @param omicsType
     * @param queryType
     * @param queryValue
     * @param significance
     * @param afterId
     * @param isCount
     * @param countMax
     * @return
     */
    @ApiSecured
    @GET
    @GZIP
    @Path("2/datapoints")
    @Produces("application/json")
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "Query Transcriptomic/Cistromic Datapoints. Returns upto 10000 datapoints.",
            notes = "Since endpoint is for both Transcriptomic and Cistromic datapoints, the fields in the results returned depend on the Omics type. " +
                    "No other filters are applied.\n\n" +
                    "Note: Instead of getting a large data payload, it is far better to do paging by utilizing the startId parameter (the data is sorted ASC by the id field). If,paging the ideal max number of datapoints to requets is 5000 records. " +
                    "Secondly, the number of datapoints returned may be higher than the specified countMax parameter, since multiple family tree hierarchies may map to the same datapoint. ",
            response = ApiOmicsDatapoint.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of Datapoints", response = ApiOmicsDatapoint.class, responseContainer = "List"),
            @ApiResponse(code = 413, message = "Payload result will be too large", response = ApiErrorMessage.class),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = ApiErrorMessage.class)
    })
    public Response findDatapointsForReactome(
            @ApiParam(value = "apiKey", required = true) @Size(max=36) @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "omicsType (Transcriptomic,Cistromic)", required=true) @Size(max=14) @QueryParam("omicsType") String omicsType,
            @ApiParam(value = "queryType (only doi supported)", required = true) @QueryParam("queryType") @Size(max=3) @DefaultValue("doi") String queryType,
            @ApiParam(value = "queryValue (The value to search on, e.g. dataset doi.)", required = true) @Size(max=20) @QueryParam("queryValue") String queryValue,
            @ApiParam(value = "significance (For Transcriptomic query only)", required = false)  @QueryParam("significance") @DefaultValue("0.05")  Double significance,
            @ApiParam(value = "startId (for multiple pages, data is sorted by id field, so provide your last highest id for the next page)", required = false) @QueryParam("startId") @DefaultValue("0")  Long afterId,
            @ApiParam(value = "isCount (If seeking only the number of datapoints meeting criteria, and not the datapoints themselves)", required = false)  @QueryParam("isCount") @DefaultValue("0") Integer isCount,
            @ApiParam(value = "countMax (Use smaller number for better performance. This is an estimate only, as the endpoint may return a number higher than countMax, see endpoint description above.)", required = false) @QueryParam("countMax") @DefaultValue("10000") Integer countMax
    );


    /**
     * FR: id,EG/symbol,cpv,gmfc,percentile
     * Return Consensome meeting parameters
     * Use seek pagination
     * @param apiKey
     * @param omicsType
     * @param doi
     * @param psId
     * @param organId
     * @param familyId
     * @param species
     * @param percentile
     * @param startId
     * @param isCount
     * @param count
     * @return
     */

    @ApiSecured
    @GET
    @GZIP
    @Path("2/consensomes")
    @Produces({ "application/json" })
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Query Consensomes",
            notes = "Query for a list of Consensomes. Provide a consensome doi  or a physiological system Id(ps)+organ Id+familyId+species combination to identify consensome." +
                    "If both doi parameter and the ps+organ+familyId+species is provided, the doi takes precedence. " +
                    "The percentile cutoff of 90% applies by default. Service returns a maximum of 10000 consensome datapoints" +
                    "It is recommended that large payload of data be retrived by paging the data. To do so utilize the startId parameter: The data is sorted ASC by the id field ",
            response = ConsensomeDatapoint.class,responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of Consensomes datapoints", response = ConsensomeDatapoint.class, responseContainer = "List"),
            @ApiResponse(code = 413, message = "Payload result will be too large", response = ApiErrorMessage.class),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = ApiErrorMessage.class)
    })
    public Response findConsensomes(
            @ApiParam(value = "apiKey", required = true) @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "omicsType (Transcriptomic,Cistromic)", required=true) @QueryParam("omicsType") String omicsType,
            @ApiParam(value = "doi (conditionally required. Only consensome DOIs apply)", required = false) @QueryParam("doi")  String doi,
            @ApiParam(value = "ps (conditionally required. -1 for 'ALL'. PhysiologicalSystem Id, see dictionary,eg endpoint /api/2/biosamples)", required = false) @QueryParam("ps")  String psId,
            @ApiParam(value = "organ (conditionally required. Organ Id, -1 for 'ALL', see dictionary, e.g. endpoint /api/2/biosamples )", required = false) @QueryParam("organ")  String organId,
            @ApiParam(value = "familyId (conditionally required. -1 for 'ALL'. Signaling pathway node family Id, e.g. see endpoint /api/2/pathwayNodeHierarchy/all.)", required = false) @QueryParam("familyId")  Long familyId,
            @ApiParam(value = "modelNodeId (Experimental: For Models, Animal, Cell and Diseases, use node id instead of familyId, e.g. see endpoint /api/2/pathwayNodeHierarchy/all)", required = false) @QueryParam("modelNodeId")  Long nodeId,
            @ApiParam(value = "species (conditionally required. One of: Human, House Mouse or Norway Rat)", required = false) @QueryParam("species")  String species,
            @ApiParam(value = "percentile", required = false)  @QueryParam("percentile")  @DefaultValue("90") Double percentile,
            @ApiParam(value = "startId", required = false) @QueryParam("startId") @DefaultValue("0")  Long startId,
            @ApiParam(value = "isCount (If seeking the number of consensomes only, and not data)", required = false)  @QueryParam("isCount") @DefaultValue("0") Integer isCount,
            @ApiParam(value = "count (max number of consensomes to return)", required = false) @QueryParam("count") @DefaultValue("10000") Integer count);


    @ApiSecured
    @GET
    @GZIP
    @Path("2/modelsAndDiseases/all")
    @Produces({"application/json"})
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "List of all SPP supported Animal, Cell Models and Diseases names",
            notes = "Returns all Animal, Cell Models and Diseases names information",
            response = ModelsNode.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns all Animal, Cell Models and Diseases names information",
                    response = ModelsNode.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response getModelAndDiseasesNodes();


    @ApiSecured
    @GET
    @GZIP
    @Path("2/pathwayNodeHierarchy/all")
    @Produces({"application/json"})
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "List of all SPP supported pathways",
            notes = "Returns all signaling pathway nodes hierarchy information",
            response = SignalingPathway.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns all signaling pathway nodes hierarchy information",
                    response = SignalingPathway.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response getAllPathways();



    /**
     * Get pathway node hierarchy from the named node
     * @param apiKey
     * @param nodeLevel
     * @param names
     * @return
     */
    @ApiSecured
    @GET
    @GZIP
    @Path("2/pathwayNodeHierarchy")
    @Produces({"application/json"})
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "List of named SPP supported signaling pathways nodes hierarchy",
            notes = "Returns all signaling pathway hierarchy information. " +
                    "Generally, avoid root level requests, i.e. nodelevel=type, as this tends to be slow, unless you know " +
                    "beforehand that it has a shallow hierarchy. If you need the whole hierarchy, you are better off calling the /api/2/pathwayNodeHierarchy/all, which is cached",
            response = SignalingPathway.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns all named signaling pathway node hierarchy information for the named node",
                    response = SignalingPathway.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 400, message = "Incorrect query params", response = ApiErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response getNamedPathways(
            @ApiParam(value = "apiKey", required = true) @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "nodelevel (type,category,cclass,family). Generally, avoid level=type", required=true) @QueryParam("nodelevel") String nodeLevel,
            @ApiParam(value = "names (single or comma separated list of names for the selected nodelevel )", required = true) @QueryParam("names")  String names);

    @ApiSecured
    @GET
    @GZIP
    @Path("2/biosamples")
    @Produces("application/json")
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "List all Physiological System and Organs (simple biosamples)",
            notes = "List of all Physiological System and Organs",
            response = PsOrgan.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All Biosamples ( or Ps/Organ) ",
                    response = PsOrgan.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public <T> List<T> getAllPsOrgans();

    @ApiSecured
    @GET
    @GZIP
    @Path("2/bsms")
    @Produces("application/json")
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "List of all named SPP Bioactive Small Molecules, BSMs",
            notes = "List all SPP named Bioactive Small Molecules",
            response = BsmView.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All SPP Named BSMs  ",
                    response = PsOrgan.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response getAllSPPBsms();

    @Deprecated
    @ApiSecured
    @GET
    @Path("datasets") //there are existing public consumers using this API, so add another instead of changing name
    @Produces("application/json")
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "Deprecated. Query datasets. Retired", notes = "Returns upto 1000 datasets.",
            response = DatasetMinimalDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of Datasets", response = DatasetMinimalDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class)
    })
    public Response queryDatasets(
            @ApiParam(value = "Entrez gene ID") @QueryParam("genes.entrezgeneID") Integer geneId,
            @ApiParam(value = "Official Gene symbol") @QueryParam("genes.symbol") String symbol,
            @ApiParam(value = "Ligand Source") @QueryParam("ligand.source") String ligandSource,
            @ApiParam(value = "Ligand ID based on source") @QueryParam("ligand.sourceID") String ligandSourceId);

}
