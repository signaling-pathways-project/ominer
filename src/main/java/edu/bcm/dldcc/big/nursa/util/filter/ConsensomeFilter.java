package edu.bcm.dldcc.big.nursa.util.filter;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;
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
import edu.bcm.dldcc.big.nursa.model.omics.ConsensomeSummary;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathwayView;
import edu.bcm.dldcc.big.nursa.services.rest.omics.OmicsServicebean;
import edu.bcm.dldcc.big.nursa.util.qualifier.NoConversationDatabase;

/**
 * Filter to Manage consensome DOIs
 * @author mcowiti
 *
 */
@WebFilter(filterName = "ConsensomeDoiRedirect", urlPatterns = {"/doi/*" }, dispatcherTypes = { DispatcherType.REQUEST })
public class ConsensomeFilter extends BaseFilter implements Filter{

	@Inject
	@NoConversationDatabase
	private EntityManager noConvoEntityManager;

	@Inject
	private OmicsServicebean omicsServicebean;

	//WEI request query.jsf and ps, organ,signalingPathway,pathwayType
	private final static String OMICS_UI_REDIRECT_URL ="/ominer/query.jsf?";

	private final static String HIST_URL="/consensome/index.jsf?doi=";
	private final static String DOI_TYPE_CONSENSOME="consensome";
    private final static String DOI_TYPE_DATASET="Dataset";
	
	private static Logger log = Logger.getLogger(ConsensomeFilter.class.getName());

	@Override
	public void destroy() {
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        boolean toRedirect=false;
        boolean isDataset=false;
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
                	if(isAConsensomeDoi(doi)){
                		redirectURL=getConsensomeRedirectUrl(doi,baseURL);
                		toRedirect=true;
                	}else{
                		//TODO call dataset redirect
                        toRedirect=true;
                        isDataset=true;
                        redirectURL=baseURL+"/datasets/?doi="+doiId;
					}

					if(!isDataset) {
                        String compareURL = requestURI + "?" + URLDecoder.decode(qs, "UTF-8");
                        if (!redirectURL.equals(compareURL)) {
                            toRedirect = true;
                        } else {
                            log.log(Level.INFO, "redirectURL[[compareURL]]=" + requestURI + "[[" + compareURL + "]]");
                            toRedirect = false;
                        }
                    }
                  }else{
                	log.log(Level.SEVERE,"You requested "+requestURI +" The DOI( "+doiId+" ) is unknown. REDIRECT to error" + "/errorPages/404.jsf");

                	redirectURL=baseURL+"/errorPages/nosuchdoi.jsf";
                	toRedirect=true;
                }
        }
        if(toRedirect){
        	response.sendRedirect(redirectURL);
        }else{
        	chain.doFilter(req, response);
        }
	}

	
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
	
	private boolean isAConsensomeDoi(DOI doi){
		return doi.getType().equalsIgnoreCase(DOI_TYPE_CONSENSOME);
	}

    private boolean isADatasetDoi(DOI doi){
        return doi.getType().equalsIgnoreCase(DOI_TYPE_DATASET);
    }
	
	
	/**
	 * if current, redirect to current TM page
	 * if past, redirect to consensome history page
	 * @param doi
	 * @param baseURL
	 * @return
	 */
	private String getConsensomeRedirectUrl(DOI doi,String baseURL){

	    Optional<ConsensomeSummary> cso=omicsServicebean.findConsensomeSummaryByDoi(doi.getDoi());
		if(!cso.isPresent()){
			log.log(Level.WARNING, "old DOI ="+doi.getDoi());
            //here meaning a valid consensome DOI entry
            //if is old version, take to download page
            //UX: !necessary but, if incoming has no version, give current version

            return new StringBuilder(baseURL)
					.append(HIST_URL).append(doi.getDoi()).toString();
		}

        ConsensomeSummary cs=cso.get();
		boolean showParams=true;
		StringBuilder sb =new StringBuilder(baseURL).append(OMICS_UI_REDIRECT_URL);

        if(showParams){
				sb.append("omicsCategory=").append(cs.getType().toLowerCase()).append("s")
                        //.append("&doi=").append(doi.getDoi()) will break the UI with  the constructed URL nonsense
                        .append("&geneSearchType=Consensome")
                        .append("&pathwayType=").append("family")
						.append("&signalingPathway=").append(cs.getKey().getFamilyId())
                        .append("&family=").append(cs.getKey().getFamily())
						.append("&numberOfDatasets=").append((cs.getNumberOfDatasets()!=null)?cs.getNumberOfDatasets():"")
						.append("&numberOfExperiments=").append((cs.getNumberOfExperiments()!=null)?cs.getNumberOfExperiments():"")
						.append("&numberOfDatapoints=").append((cs.getNumberDatapoints()!=null)?cs.getNumberDatapoints():"")
				.append("&ps=").append(cs.getKey().getPsId())
				.append("&organ=").append(cs.getKey().getOrganId())
				.append("&species=").append(cs.getKey().getSpecies())
                .append("&version=").append(cs.getVersion());
		}else{
            //will break the UI with the constructed URL nonsense
			sb.append("doi=").append(doi.getDoi());
		}
		return sb.toString();
	}

	private Integer getThisDoisVersion(String doi){
		try{
			return Integer.parseInt(doi.substring(doi.lastIndexOf(".") + 1).trim());
		}catch (Exception e){
			return null;
		}
	}

    private String getNonVersionedDoi(String doi){
          return (doi.substring(0,doi.lastIndexOf(".")).trim());
    }
}
