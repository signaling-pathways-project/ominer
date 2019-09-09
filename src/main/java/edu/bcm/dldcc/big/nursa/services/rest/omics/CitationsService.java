package edu.bcm.dldcc.big.nursa.services.rest.omics;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/omics/citations/")
public interface CitationsService {

    /**
     * Download dataset in EndNote format
     * @param datasetId
     * @return
     */
    @GET
    @Path("download/dataset/{datasetId}")
    @Produces("application/x-research-info-systems")
    Response downloadDatasetAsEndNote(@PathParam("datasetId") String datasetId);

}
