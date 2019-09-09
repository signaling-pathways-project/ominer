package edu.bcm.dldcc.big.nursa.util.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * If we provide an endpoint for a DOI, we may filter via this
 * @author mcowiti
 *
 */
//@Provider
public class ConsensomeJaxRsDoiFilter implements ContainerRequestFilter {

    @Override
     public void filter(ContainerRequestContext requestContext) {
       /* MultivaluedMap<String, String> pathParameters = requestContext.getUriInfo().getPathParameters();
        pathParameters.get("doi");
       */
    }
    

}
