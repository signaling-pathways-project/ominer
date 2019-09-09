package edu.bcm.dldcc.big.nursa.util.filter;

import java.io.IOException;
import java.util.logging.Logger;

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

/**
 * Filter to intercept calls from publications citing Nursa datasets.
 * The filter simply redirects(or forward)  to the general Nursa doi filter.
 * Add additional parameter, show, is added to distinguish this kind of request from others.
 * FIXME this is really overtaken by global usage of DOIs
 * @author mcowiti
 *
 */
@WebFilter(filterName = "nursaCitationRedirect", urlPatterns = { "/cite/*","/api/cite/*" }, dispatcherTypes = { DispatcherType.REQUEST })
public class NursaPaperCitationFilter extends BaseFilter implements Filter {

	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.util.filter.NursaPaperCitation");
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        String baseURL = request.getContextPath();
        String doi = request.getParameter("doi");
        
        if(!cleanDoi(doi)){
        	response.sendError(HttpServletResponse.SC_NOT_FOUND);
        	return;
        }
        
        //TODO make sure has a valid  query id
        if(doi!=null && !doi.trim().equals("")){
        	////Forward is faster but !work: 
        	//req.setAttribute("doi", doi);
        	//req.getRequestDispatcher("/index.jsf").forward(req, response);
        	/////
        	log.info("Nursa dataset publication citation for doi = "+doi);
        	response.sendRedirect("/datasets/index.jsf?doi="+doi+"&show=1");
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
