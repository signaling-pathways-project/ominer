package edu.bcm.dldcc.big.nursa.util.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.util.qualifier.NoConversationDatabase;

/**
 * DOI filters
 * Note: two DOI filters, and  that are not yet consolidated
 * @author mcowiti
 * Original catch all jsf: urlPatterns = { "*.jsf"}
 */
@WebFilter(filterName = "DoiRedirect", 
urlPatterns = {"/datasets/*","/molecules/*","/index.jsf*"},
dispatcherTypes = { DispatcherType.REQUEST })
public class DoiRedirect  extends BaseFilter implements Filter{

	@Inject
	@NoConversationDatabase
	private EntityManager noConvoEntityManager;
	
	private final static String DOI_TYPE_CONSENSOME="consensome";
    private final static String CONSENSOME_DOI_URL="/doi/?doi=";
	
	private static Logger log = Logger.getLogger(DoiRedirect.class.getName());

	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        boolean show= request.getParameter("show") != null;
        boolean toRedirect=false;
        String baseURL = request.getContextPath();
        String redirectURL=baseURL;
        String doiId = request.getParameter("doi");
        

        if(!cleanDoi(doiId)){
        	response.sendError(HttpServletResponse.SC_NOT_FOUND);
        	return;
        }
        
        if ((doiId != null) && (!doiId.equals(""))) 
        {
                String requestURI = request.getRequestURI();
                String qs = request.getQueryString();
                DOI doi = noConvoEntityManager.find(DOI.class, doiId);
                if (doi != null) 
                {
                	//make sure we're using an absolute URL
                    //if this URL really necessary
					log.log(Level.INFO,"doi url="+doi.getUrl());
                	redirectURL = baseURL + (doi.getUrl().charAt(0)=='/'?doi.getUrl():"/"+doi.getUrl());
                	String compareURL = requestURI+"?"+URLDecoder.decode(qs,"UTF-8");
                	
                	redirectURL=(!show)?redirectURL:redirectURL+"&show=1";
                	//Don't redirect if are already at the correct place.
                        if (!redirectURL.equals(compareURL)) {
                        	toRedirect=true;
                        }
                        if(isAConsensomeDoi(doi)){
                    		toRedirect=true;
							redirectURL=baseURL+CONSENSOME_DOI_URL+doiId;
                    	}
                }else{
                	log.log(Level.SEVERE,"You requested "+requestURI +" The DOI( "+doiId+" ) unknown. REDIRECT to error" + "/errorPages/404.jsf");
                	redirectURL=baseURL+"/errorPages/nosuchdoi.jsf";
                	toRedirect=true;
                }
        }
        if(toRedirect){
        	log.log(Level.FINE,"REDIRECT=" + redirectURL);
            response.sendRedirect(redirectURL);
        }else{
        	chain.doFilter(req, response);
        }
	}
	
	@Override
	public void init(FilterConfig arg0) {

	}
	
	private boolean isAConsensomeDoi(DOI doi){
		return doi.getType().equalsIgnoreCase(DOI_TYPE_CONSENSOME);
	}
}
