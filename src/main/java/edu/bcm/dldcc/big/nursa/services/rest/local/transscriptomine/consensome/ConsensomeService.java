package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.consensome;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;

import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;
import edu.bcm.dldcc.big.nursa.model.omics.dto.ConsensomeDataWrap;
import edu.bcm.dldcc.big.nursa.model.omics.Consensome;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Consensome data service
 * TODO: Move to ms
 * @author mcowiti
 *
 */
//FIXME @Path("/micros/")
//FIXME @Api(value = "/micros/", tags = "Consensome Service")
public interface ConsensomeService {

	@GET
    @Path("v1/consensome/summaries")
    @Produces({ "application/json" })
	 @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Show all active/current summaries", notes = "Show all active/current summaries.", 
	 response = ConsensomeSummary.class, responseContainer = "List")
    @ApiResponses(value = { 
		@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
		@ApiResponse(code = 200, message="Summary Info", response=ConsensomeSummary.class) })
	public Response findSummaries();
	
	@GET
    @Path("v1/consensome/summary")
    @Produces({ "application/json" })
	 @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Show relevant active summary", notes = "Show  active/current summary.", 
	 response = ConsensomeSummary.class)
    @ApiResponses(value = { 
		@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
		@ApiResponse(code = 200, message="Summary", response=ConsensomeSummary.class) })
	public Response findSummary(
			@ApiParam(value = "pw", required = true) @QueryParam("pw")  String pathway,
			@ApiParam(value = "ps", required = true) @QueryParam("ps")  String ps,
			@ApiParam(value = "o", required = true) @QueryParam("o")  String organ,
			@ApiParam(value = "s", required = true) @QueryParam("s")  String species,
			@ApiParam(value = "version", required = false) @QueryParam("v")  String date);
 
	@GET
    @Path("v1/consensome/summary/doi")
    @Produces({ "application/json" })
	 @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Find  active summary by DOI", notes = "Show  active/current summary by DOI.", 
	 response = ConsensomeSummary.class)
    @ApiResponses(value = { 
		@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
		@ApiResponse(code = 200, message="Summary by DOI", response=ConsensomeSummary.class) })
	public Response findSummary(
			@ApiParam(value = "DOI", required = true) @QueryParam("doi")  String doi);
 
	@GET
    @Path("v1/consensome/data/countDatapoints")
    @Produces({ "application/json" })
	 @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Show countDatapoints of active  data", notes = "Show countDatapoints of active  consensome data.",
	 response = Integer.class)
    @ApiResponses(value = { 
		@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
		@ApiResponse(code = 200, message="Count data", response=Integer.class) })
	public Response countConsensome(
			@ApiParam(value = "DOI", required = true) @QueryParam("doi")  String doi,
			@ApiParam(value = "pw", required = true) @QueryParam("pw")  String pathway,
			@ApiParam(value = "ps", required = true) @QueryParam("ps")  String ps,
			@ApiParam(value = "o", required = true) @QueryParam("o")  String organ,
			@ApiParam(value = "s", required = true) @QueryParam("s")  String species,
			@ApiParam(value = "v", required = false)  @QueryParam("v")  String date);
	
	@GET
    @Path("v1/consensome/data")
    @Produces({ "application/json" })
	 @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Show active or archived data", notes = "Show active or archived consensome data.", 
	 response = Consensome.class, responseContainer = "List")
    @ApiResponses(value = { 
		@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
		@ApiResponse(code = 200, message="Consensome data", 
		response=Consensome.class, responseContainer = "List") })
	public Response findConsensome(
			@ApiParam(value = "DOI", required = true) @QueryParam("doi")  String doi,
			@ApiParam(value = "pw", required = true) @QueryParam("pw")  String pathway,
			@ApiParam(value = "ps", required = true) @QueryParam("ps")  String ps,
			@ApiParam(value = "o", required = true) @QueryParam("o")  String organ,
			@ApiParam(value = "s", required = true) @QueryParam("s")  String species,
			@ApiParam(value = "countDatapoints", required = false)  @QueryParam("countDatapoints")  Integer count,
			@ApiParam(value = "v", required = false)  @QueryParam("v")  String date);
	
	@GET
    @Path("v1/consensome/datalist")
    @Produces({ "application/json" })
	 @Cache(maxAge=86400,sMaxAge=86400)
    @ApiOperation(value = "Show active  data with countDatapoints", notes = "Show active consensome data with countDatapoints.",
	 response = ConsensomeDataWrap.class)
    @ApiResponses(value = { 
		@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
		@ApiResponse(code = 200, message="Consensome data with countDatapoints",
		response=ConsensomeDataWrap.class,responseContainer = "List") })
	public Response findConsensomesWithCount(
			@ApiParam(value = "DOI", required = true) @QueryParam("doi")  String doi,
			@ApiParam(value = "pw", required = true) @QueryParam("pw")  String pathway,
			@ApiParam(value = "ps", required = true) @QueryParam("ps")  String ps,
			@ApiParam(value = "o", required = true) @QueryParam("o")  String organ,
			@ApiParam(value = "s", required = true) @QueryParam("s")  String species,
			@ApiParam(value = "countDatapoints", required = false)  @QueryParam("countDatapoints")  Integer count,
			@ApiParam(value = "page", required = false)  @QueryParam("page")  Integer page,
			@ApiParam(value = "v", required = false)  @QueryParam("v")  String date);
	
	
	@GET
    @Path("v1/consensome/excel")
    @Produces("application/vnd.ms-excel")
	@NoCache
	@ApiOperation(value = "Download  active or archived data", notes = "Download active or archived consensome data.", 
	 response = Response.class)
   @ApiResponses(value = { 
		@ApiResponse(code = 403, message="Invalid credentials", response=String.class),
		@ApiResponse(code = 200, message="File data", response=String.class) })
    public Response downloadExcelFile(
    		@ApiParam(value = "pw", required = true) @QueryParam("pw")  String pathway,
			@ApiParam(value = "ps", required = true) @QueryParam("ps")  String ps,
			@ApiParam(value = "o", required = true) @QueryParam("o")  String organ,
			@ApiParam(value = "s", required = true) @QueryParam("s")  String species,
			@ApiParam(value = "v", required = false)  @QueryParam("v")  String version,
			@ApiParam(value = "countDatapoints", required = false)  @QueryParam("countDatapoints")  Integer count,
			@ApiParam(value = "email", required = false)  @QueryParam("email") String email);
}
