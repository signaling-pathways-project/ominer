package edu.bcm.dldcc.big.nursa.services.rest.pubapi;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import edu.bcm.dldcc.big.nursa.services.rest.omics.DatasetDownloadBean;
import org.jboss.resteasy.annotations.cache.Cache;

@Path("/cache/")
public class TestCache {

	//@Context ServerCache restCache;

	@Inject
	private DatasetDownloadBean datasetDownloadBean;

	@GET
    @Path("test/")
    @Produces("application/json")
	@Cache(maxAge=60,sMaxAge=60)
	public Response test(@QueryParam("type") String type){
		
		System.out.println("@test");
		return Response.ok("ICHYN").build();
	}

}
