package edu.bcm.dldcc.big.nursa.services.rest.pubapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.Cache;

@Path("/cache/")
public class TestCache {

	//@Context ServerCache restCache;
	
	@GET
    @Path("test/")
    @Produces("application/json")
	@Cache(maxAge=60,sMaxAge=60)
	public Response test(@QueryParam("type") String type){
		
		System.out.println("@test");
		return Response.ok("ICHYN").build();
	}
}
