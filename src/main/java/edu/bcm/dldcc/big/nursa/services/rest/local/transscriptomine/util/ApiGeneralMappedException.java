package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Api General exception Mapper
 * @author mcowiti
 *
 */
@Provider
@Produces("application/json")
public class ApiGeneralMappedException implements ExceptionMapper<Exception> {

    @Override
    @Produces("application/json")
    public Response toResponse(Exception e) {        
    	return Response.serverError().entity(e.getMessage()).build();
    }
}
