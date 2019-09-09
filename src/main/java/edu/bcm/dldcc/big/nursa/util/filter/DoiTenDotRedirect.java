package edu.bcm.dldcc.big.nursa.util.filter;

import edu.bcm.dldcc.big.nursa.model.common.DOI;
import edu.bcm.dldcc.big.nursa.util.qualifier.NoConversationDatabase;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DOI 10.1621 filters, for lazy users who use/typein DOI in URL
 * Note: Just forward to DOI  filters,
 * @author mcowiti
 */
@WebFilter(filterName = "DoiTenDotRedirect",
urlPatterns = {"/10.1621/*"},
dispatcherTypes = { DispatcherType.REQUEST })
public class DoiTenDotRedirect extends BaseFilter implements Filter{

	@Inject
	@NoConversationDatabase
	private EntityManager noConvoEntityManager;
	
	private final static String DATASET_DOI_URL="/datasets/?doi=10.1621/";
	
	private static Logger log = Logger.getLogger(DoiTenDotRedirect.class.getName());

	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String baseURL = request.getContextPath();
        String redirectURL=baseURL;
        String doiId = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);

        log.log(Level.INFO,"half doi="+doiId);

        if(!cleanHalfDoi(doiId)){
        	response.sendError(HttpServletResponse.SC_NOT_FOUND);
        	return;
        }
        
        if ((doiId != null) && (!doiId.equals(""))) 
        {
            log.log(Level.FINE,"a DOI in URL, REDIRECT=" + redirectURL);
            redirectURL=baseURL+DATASET_DOI_URL+doiId;
            response.sendRedirect(redirectURL);
        }else{
        	chain.doFilter(req, response);
        }
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
