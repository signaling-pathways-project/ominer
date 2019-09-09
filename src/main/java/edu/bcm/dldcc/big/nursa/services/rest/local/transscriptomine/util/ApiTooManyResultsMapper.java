package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Too many results would be returned Exception Mapper
 * @author mcowiti
 *
 */
@Provider
@Produces("application/json")
public class ApiTooManyResultsMapper implements ExceptionMapper<TooManyResultsException> {

    @Override
    @Produces("application/json")
    public Response toResponse(TooManyResultsException e) {        
    	ApiApplicationErrorMessage msg=
    			new ApiApplicationErrorMessage(
    					ApiApplicationErrorMessage.TOO_MANY_RESULTS,e.getMessage());
    	return Response.status(413).entity(msg).build();
    }
}
