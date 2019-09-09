package edu.bcm.dldcc.big.nursa.services.rest.pubapi.legacy;

import edu.bcm.dldcc.big.nursa.model.cistromic.DatasetViewMiniDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetMinimalDTO;
import edu.bcm.dldcc.big.nursa.util.qualifier.ApiSecured;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/apii/0/")
@Api(value = "/apii/0/", tags = "Signalingpathway Project public API (v0), alpha evals")
public interface ApiEvals {

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

}
