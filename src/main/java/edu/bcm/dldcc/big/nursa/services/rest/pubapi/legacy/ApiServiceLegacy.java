package edu.bcm.dldcc.big.nursa.services.rest.pubapi.legacy;

import edu.bcm.dldcc.big.nursa.model.cistromic.DatasetViewMiniDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetMinimalDTO;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.DatapointDTO;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util.ApiApplicationErrorMessage;
import edu.bcm.dldcc.big.nursa.util.qualifier.ApiSecured;
import io.swagger.annotations.*;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/api/1/")
@Api(value = "/api/1/", tags = "Signalingpathway Project public API (v1)")
public interface ApiServiceLegacy {


    @ApiSecured
    @GET
    @Path("info")
    @Produces({"application/json"})
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "Info ", notes = "Returns upto 1000 datasets.",response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "Info Message", response = DatasetViewMiniDTO.class, responseContainer = "List"),
    })
    public String info();


    @Deprecated
    @ApiSecured
    @GET
    @Path("test/datasets")
    @Produces({"application/json"})
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "Query datasets (v1). To be retired soon", notes = "Returns upto 1000 datasets.",
            response = DatasetViewMiniDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of Datasets", response = DatasetViewMiniDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class)
    })
    public Response findTestDatasetsForReactome(@QueryParam("sdate") String sdate,@QueryParam("count") Integer count);


    @Deprecated
    @ApiSecured
    @GET
    @Path("datasets")
    @Produces({"application/json"})
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "Query datasets (v1). To be retired soon", notes = "Returns upto 1000 datasets.",
            response = DatasetMinimalDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of Datasets", response = DatasetMinimalDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class)
    })
    public Response findDatasets(
            @ApiParam(value = "Entrez Gene ID") @QueryParam("genes.entrezgeneID") Integer geneId,
            @ApiParam(value = "Official Genes symbol") @QueryParam("genes.symbol") String symbol,
            @ApiParam(value = "Ligand Source (CAS,PubChem,Chebi or IUPHAR)") @QueryParam("ligand.source") String ligandSource,
            @ApiParam(value = "ligand ID based on naming source") @QueryParam("ligand.sourceID") String ligandSourceId);


    @Deprecated
    @GET
    @ApiSecured
    @Path("1/datapoints/query/")
    @Produces({"application/json"})
    @Cache(maxAge = 86400, sMaxAge = 86400)
    @ApiOperation(value = "Query datapoints. To be retired soon", notes = "Returns upto 2000 datapoints. Defaults are applied for FoldChangeView (>=2 or <=0.5), Significane (<=0.05) and Direction (any)",
            response = DatapointDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Invalid apiKey", response = String.class),
            @ApiResponse(code = 200, message = "List of Datapoints", response = DatapointDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = ApiApplicationErrorMessage.class)
    })
    public Response getDatapointsResponse(
            @ApiParam(value = "apiKey", required = true) @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "Include Experiments? Ignored", required = false) @QueryParam("includeExp") String includeExp,
            @ApiParam(value = "gene by (gene,goTerm,disease)", required = false) @QueryParam("geneSearchType") String geneSearchType,
            @ApiParam(value = "Gene to find") @QueryParam("gene") String gene,
            @ApiParam(value = "Disease Term ") @QueryParam("disease") String disease,
            @ApiParam(value = "GoTerm") @QueryParam("goTerm") String goTerm,
            @ApiParam(value = "Molecule") @QueryParam("molecule") String molecule,
            @ApiParam(value = "ligandIdType") @QueryParam("ligandIdType") String ligandIdScheme,
            @ApiParam(value = "significance") @QueryParam("significance") @DefaultValue("0.05") String significance,
            @ApiParam(value = "foldChange") @QueryParam("foldChange") String foldChange,
            @ApiParam(value = "direction") @QueryParam("direction") String direction,
            @ApiParam(value = "species taxon Id") @QueryParam("species") String species,
            @QueryParam("doi") String doi,
            @ApiParam(value = "molecule Treatment Time") @QueryParam("moleculeTreatmentTime") String treatmenttime,
            @ApiParam(value = "Physiological System") @QueryParam("ps") String ps,
            @ApiParam(value = "Organ") @QueryParam("organ") String organ,
            @ApiParam(value = "Tissue") @QueryParam("tissue") String tissue,
            @ApiParam(value = "Return countDatapoints, no records") @QueryParam("findCount") Integer getCount,
            @ApiParam(value = "countDatapoints") @QueryParam("countDatapoints") Integer count,
            @ApiParam(value = "Min fold Change (foldChange ignored)") @QueryParam("foldChangeMin") String foldChangeMin,
            @ApiParam(value = "Max fold Change (foldChange ignored)") @QueryParam("foldChangeMax") String foldChangeMax,
            @ApiParam(value = "signaling Pathway") @QueryParam("signalingPathway") String signalingPathway,
            @ApiParam(value = "pathway Category") @QueryParam("pathwayCategory") String pathwayCategory
    ) throws Exception;
}
