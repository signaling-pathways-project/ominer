package edu.bcm.dldcc.big.nursa.util.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import edu.bcm.dldcc.big.nursa.model.omics.TranscriptomineApiKey;
import edu.bcm.dldcc.big.nursa.services.ApiAccessService;
import edu.bcm.dldcc.big.nursa.util.qualifier.ApiSecured;

/**
 * API access interceptor
 * This checks authorization and apiKey limit allowance
 * To prevent general server overload, use specialized methods, eg Nginx infront of apache
 * @author mcowiti
 *
 */
//Not needed @Provider
public class ApiAccessSecurityInterceptor implements javax.ws.rs.container.ContainerRequestFilter  {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.util.filter.ApiAccessSecurityInterceptor");
	
	@Context private HttpServletRequest httpRequest;
	
	@Inject private ApiAccessService apiBean;
	
	private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<Object>());;
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403, new Headers<Object>());;
    private static final ServerResponse TOO_MANY_REQUESTS = new ServerResponse("Too Many Requests (Enhance your calm)", 429, new Headers<Object>());;
    
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		 ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
	     Method method = methodInvoker.getMethod();
	     
	     if(  method.isAnnotationPresent(ApiSecured.class))
	      {
	    	 final String key =httpRequest.getParameter(ApiAccessService.API_KEY_PARAM);
	    	 
	           log.log(Level.INFO," apiKey : "+key); 
	         
	           if(key == null || key.equals("")) {
	        	   requestContext.abortWith(ACCESS_DENIED);
	        	   return;
	           }else{
	        	   TranscriptomineApiKey apiKeyAcct=apiBean.isValidApiKey(key);
	        	   if(apiKeyAcct == null){
	        		   log.log(Level.WARNING," ACCESS_DENIED apiKey : "+key); 
	        		   requestContext.abortWith(ACCESS_DENIED);
	        		   return;
	        	   }else{
	        		   if(!apiBean.mayMakeNewAPICall(apiKeyAcct)){
	        			   log.log(Level.WARNING," TOO_MANY_REQUESTS apiKey : "+key); 
	        			   requestContext.abortWith(TOO_MANY_REQUESTS);
	    	        	   return;
	        		   }
	        	   }
	        		   
	           }
	    	 
	      }
	}

}
