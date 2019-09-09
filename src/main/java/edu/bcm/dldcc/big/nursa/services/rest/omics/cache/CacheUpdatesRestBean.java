package edu.bcm.dldcc.big.nursa.services.rest.omics.cache;

import edu.bcm.dldcc.big.nursa.model.cistromic.PathwayNodesView;
import edu.bcm.dldcc.big.nursa.util.qualifier.ApiSecured;
import io.swagger.annotations.*;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Internal use only services to manage Families (BSM->Node, Node->Family) changes
 * @author mcowiti
 */
@Path("/10net/")
public class CacheUpdatesRestBean {

    @Inject
    Event<PathwayNodesView> cacheEvent;


    /**
     * Family Node cache was updated
     * (entry changed, active flag changed, new node-family mapping or BSM->Node changed)
     * @param apiKey
     * @param cache
     * @return
     */
    @GET
    @ApiSecured
    @Path("1/cache/updated/families")
    @Produces({"application/json"})
    @ApiOperation(value = "Initiate HashMap Cache updates", notes = "Initiate HashMap Cache update",
            response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 403, message="Invalid apiKey", response=String.class),
            @ApiResponse(code = 200, message="List of Datapoints", response=Response.class),
            @ApiResponse(code = 400, message = "Invalid input parameters.", response = String.class)
    })
    public Response getDatapointsResponse(
            @ApiParam(value = "apiKey", required = true)  @QueryParam("apiKey") String apiKey,
            @ApiParam(value = "cache", required = false) @QueryParam("cache") String cache){

        this.cacheEvent.fire(new PathwayNodesView());
        return Response.status(200).entity("Cache updated scheduled for "+cache).build();

    }

}
