package edu.bcm.dldcc.big.nursa.services.rest.omics;

import org.jboss.resteasy.annotations.cache.Cache;
//import org.jboss.resteasy.plugins.cache.server.ServerCache;

import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;
import edu.bcm.dldcc.big.nursa.model.omics.Tissue;
import edu.bcm.dldcc.big.nursa.model.omics.dto.NursaDatasetDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.PsOrgan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.util.List;

/**
 * The Transcriptomine   data9constrained) lookup Service
 * It is safe and recommended for clients to cache the result of this WS.
 * Service returns the constrained TM data Types ( Lookup ) used by subsequent queries
 *
 * @author mcowiti
 */
@Path("/transcriptomine/lookup")
@Api(value = "/transcriptomine/lookup",tags="Transcriptomine Lookup")
public interface TranscriptomineLookupService {

    /**
     * Verifies the validity of the Genelist uploaded.
     * Return the number of invalid genes found
     *
     * @param genelist
     * @return the number of invalid genes found
     */
    @POST
    @Path("/genelist")
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Verify validity of a Gene List",notes = "Verifies validity of GeneList. 0=OK",
    response = Integer.class)
    @ApiResponses(value = { 
			@ApiResponse(code = 200, message="Number (integer) of invalid Genes found", 
					response=SignalingPathway.class),
			@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
   public Integer verifyGeneList(String genelist);

    @GET
    @Path("/allTissues")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "List all Tissues",notes = "List all Tissues",
    response = Tissue.class, responseContainer = "List")
    @ApiResponses(value = { 
			@ApiResponse(code = 200, message="All Tissues", 
					response=SignalingPathway.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public <T> List<T> getAllTissuesAndCellLines();

    @GET
    @Path("/allTissuesCategories")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Find default Tissue Categories",notes = "Find default Tissue Categories",
    response = PsOrgan.class, responseContainer = "List")
    @ApiResponses(value = { 
			@ApiResponse(code = 200, message="All Tissues and Categories", 
					response=Tissue.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
   public <T> List<T> getAllTissuesCategories();

    
    @GET
    @Path("/pathways")
    @Produces({"application/json"})
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Find named pathways; Comma separate multiple pathway names",
    notes = "Supply type and one or more pathway names. Comma separate pathway names",
    response = SignalingPathway.class, responseContainer = "List")
    @ApiResponses(value = { 
			@ApiResponse(code = 200, message="Returns Pathways information of given Type and names", 
					response=SignalingPathway.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response getNamedPathways(
    		@ApiParam(value = "type", required = false) @QueryParam("type") String type,
    		@ApiParam(value = "names", required = true) @QueryParam("names") String name);
    
    
    @GET
    @Path("/allSignalingPathways") 
    @Produces({"application/json"})
    @Cache(maxAge=3600,sMaxAge=3600)
    @ApiOperation(value = "Find all (default) pathways",notes = "Find all(default) pathways",
    response = SignalingPathway.class, responseContainer = "List")
    @ApiResponses(value = { 
			@ApiResponse(code = 200, message="All Pathways",
					response=SignalingPathway.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public  List<SignalingPathway> getAllSignalingPathways();
    
    
    @GET
    @Path("/relatedDatasets")
    @Produces("application/json")
    @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Find related Datasets",
    notes = "Find Datasets related to given dataset Id",
    response = NursaDatasetDTO.class, responseContainer = "List")
    @ApiResponses(value = { 
			@ApiResponse(code = 200, message="List of Datasets related to the given datasetId, using the given 'by' relationship", 
					response=NursaDatasetDTO.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Incorrect query params", response = String.class),
			@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
   public <T> List<T> getRelatedDataset(
    		@ApiParam(value = "by", required = false)  @QueryParam("by") @DefaultValue("0") Integer by,
    		@ApiParam(value = "dsId", required = false)  @QueryParam("dsId") @DefaultValue("0") Long datasetId,
    		@ApiParam(value = "doi", required = false)  @QueryParam("doi") @DefaultValue("0") String doi,
    		@ApiParam(value = "countDatapoints", required = false)  @QueryParam("countDatapoints") @DefaultValue("50") Integer count);
    
    
}
