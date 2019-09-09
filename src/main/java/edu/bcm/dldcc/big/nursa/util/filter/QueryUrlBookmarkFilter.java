package edu.bcm.dldcc.big.nursa.util.filter;

import java.io.IOException;
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

import edu.bcm.dldcc.big.nursa.util.qualifier.NoConversationDatabase;

/**
 * Suspect: looks like was a test 
 * Filter to redirect bookmarked Query URL, /transcriptomine/query/{ID}
 * Find query by ID, redirect to Query UI. 
 * If no query id, but has valid query params, extract and create Query Object
 * If no valid query Id, or query params, show beginning TM page
 * @author mcowiti
 *
 */
@WebFilter(filterName = "bookmarkedQueryUrlRedirect", urlPatterns = { "/transcriptomine/query/*" }, dispatcherTypes = { DispatcherType.REQUEST })
public class QueryUrlBookmarkFilter implements Filter {

	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.util.filter.QueryUrlBookmarkFilter");
	
	
	@Inject
	@NoConversationDatabase
	private EntityManager noConvoEntityManager;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        //find ID, redirect to Query URL/controller
        String baseURL = request.getContextPath();
         //TODO make sure has a valid  query id
       if(baseURL.matches(".+")){
        	//FIXME may also use this to gather query params, create query and redirect 
    	   String target="/ominer/index.jsf#eee4da61-34bf-410a-93a5-af2413f0bcb4";
        	response.sendRedirect(baseURL+target);//"/molecule/nr.jsf?doi=10.1621/D7JZGQS9BU");
        }
         
        chain.doFilter(req, response);

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
