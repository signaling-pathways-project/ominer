package edu.bcm.dldcc.big.nursa.services.rest.omics;

import io.swagger.annotations.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * @author mcowiti
 */
@Path("/omics/")
@Api(value = "/omics/",tags="General Omics Services (Internal)")
public interface BsmRestService {

    @GET
    @Path("bsm/{symbol}")
    @Produces("application/json")
    @ApiOperation(value = "Provide  BSM report Info",notes = "Supply Known BSM symbol")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message="Get BSM Report info"),
            @ApiResponse(code = 400, message = "Incorrect BSM symbol path param", response = String.class),
            @ApiResponse(code = 404, message = "Unknown BSM symbol path param", response = String.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response getBsmInfoReport(
            @ApiParam(value = "symbol", required = true)  @PathParam("symbol") String symbol);

}
