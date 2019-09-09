package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.util;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * No such identifier exception Mapper
 * @author mcowiti
 *
 */
@Provider
@Produces("application/json")
public class ApiNoSuchIdentifierExceptionMapper implements ExceptionMapper<NoSuchIdentifierException> {

    @Override
    @Produces("application/json")
    public Response toResponse(NoSuchIdentifierException e) {        
    	ApiApplicationErrorMessage msg=
    			new ApiApplicationErrorMessage(
    					ApiApplicationErrorMessage.NO_SUCH_IDENTIFIER,e.getMessage());
    	return Response.status(406).entity(msg).build();
    }
}
