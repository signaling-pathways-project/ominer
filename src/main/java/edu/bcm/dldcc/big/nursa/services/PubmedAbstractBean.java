package edu.bcm.dldcc.big.nursa.services;

import edu.bcm.dldcc.big.nursa.model.common.Article;
import edu.bcm.dldcc.big.nursa.model.common.Article_;
import edu.bcm.dldcc.big.nursa.model.common.Reference;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.NcbiServicesBean;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SLSB  to retrieve  pubmed abstracts
 * The pubmed Abstract is either cached or pulled from NCBI
 * @author mcowiti
 *
 */

@Stateless
@Named("pubmedAbstractBean")
@LocalBean
public class PubmedAbstractBean extends BaseArticleBean {

	private static final long serialVersionUID = 1190571257290905898L;

	@Inject private NcbiApiCallsTractorBean ncbiApiCallsTractorBean;
	
	@Inject 
	@UserDatabase
	private EntityManager objectEntityManager;
	 
	private static Logger log = Logger.getLogger("edu.bcm.dldcc.big.nursa.util.restapi.client.PubmedAbstractBean");
	 
	@Inject
	private NcbiServicesBean ncbiPubmedService;
	
	private String abstractText;
	private boolean useHttpConnection=false;//TODO change to true if desire that
	
	public void prepareAbstract(Reference reference){
		this.abstractText=this.getPubmedAbstract(reference);
	}
	
	public Article findArticle(String pubmedid)
	   {
		   Article entity;
		      try
		      {
		          CriteriaBuilder cb = objectEntityManager.getCriteriaBuilder();
			      	CriteriaQuery<Article> cq = cb.createQuery(Article.class);
					Root<Article> article = cq.from(Article.class);
					cq.where(cb.equal(article.get(Article_.pubmedId),pubmedid)); 
					cq.select(article);
					entity = objectEntityManager.createQuery(cq).getSingleResult();
		      }
		      catch (NoResultException nre)
		      {
		         entity = null;
		      }
		      return entity;
	   }
	/**
	 * 
	 * Look for pubmed  abstract.
	 * Call Utilized by Nursa3 pubmed UI
	 * First check cached. If none, call ncbi API and cache results in persistence
	 * @param reference
	 * @return
	 */
	public String getPubmedAbstract(Reference reference ){
		
		
		if(reference == null || reference.getArticle() == null){
			log.log(Level.WARNING,"@getPubmedAbstract null reference object"); 
			return null;
		}
		
		if(reference.getArticle().getAbstractBlurb() != null){
			return reference.getArticle().getAbstractBlurb();
		}
		
		String abstratctText=null;
		try {
			abstratctText=getAbstractText(reference.getPubmedId());
		} catch (NcbiRestMaxConnectionsException e) {
			log.log(Level.WARNING,"The abstract service is currently busy for paper: "+reference.getPubmedId());
			return "The abstract service is currently busy. Please try again shortly. Thank you for your patience.";
		}
		
		if(abstratctText == null || abstratctText.trim().equals("")){
			log.log(Level.WARNING,"No abstract available  for paper: "+reference.getPubmedId());
			return "No Abstract is available at this time for pubmedId: "+reference.getPubmedId();
		}
		
		// cache
		reference.getArticle().setAbstractBlurb(abstratctText);
		try{
			objectEntityManager.merge(reference);
		}catch (Exception e){
			log.log(Level.SEVERE,"Failed to persist abstract for paper: "+reference.getPubmedId());
		}
		
		return abstratctText;
	}
	
	/**
	 * Get Abstract Text
	 * Call Utilized by Transcriptomine though REST WS call
	 * @param pmid
	 * @return
	 * @throws NcbiRestMaxConnectionsException
	 */
	public String getAbstractText(String pmid) 
			throws NcbiRestMaxConnectionsException{
		
		String abstractText=null;
		try {
			
			if(!ncbiApiCallsTractorBean.mayMakeNewNCPIAPICall())
			{
				log.warning("The Abstract Service is currently busy. Please try again in a couple of  seconds. Thank you for your patience. "+pmid);
				throw new NcbiRestMaxConnectionsException("The Abstract Service is currently busy. Please try again in a couple of  seconds. Thank you for your patience. "+pmid);
				// FUTURE need schedule/use event for time sychronous receive . UI wait max total 8 seconds fro data
			}
			
			abstractText =(useHttpConnection)?
					this.ncbiPubmedService.getAbstractFromNcbiByHttpConnection(pmid):
				this.ncbiPubmedService.getAbstractFromNcbiByClient(pmid);
			
			ncbiApiCallsTractorBean.setCount(ncbiApiCallsTractorBean.getCount()-1);
			
		} catch (Exception e) {
			log.warning("There was a problem locating this Abstract. No Abstract is available at this time for pubmed "+pmid);
			e.printStackTrace();
			return null;
		}
		return abstractText;
	}
	
	public String initialCitation(Reference reference){
		StringBuilder sb= new StringBuilder();
		
		if(reference == null)
			return null;
		
		if(reference.getArticle() == null || reference.getArticle().getAuthorsList() == null)
			return null;
		
		String authors=getCitationAuthors(reference.getArticle().getAuthorsList());
		
		sb.append(authors).append(" ").append("(").append(reference.getArticle().getPublishYear()).append(")").append(" ")
		   .append(reference.getArticle().getArticleTitle()).append(" ");
		if (reference.getArticle().getJournal()!=null)
			sb.append(reference.getArticle().getJournal().getISOAbbreviation()).append(" ");
		return sb.toString();
	}

	public String authorCitation(Reference reference){
		StringBuilder sb= new StringBuilder();
		
		if(reference == null)
			return null;
		
		if(reference.getArticle() == null || reference.getArticle().getAuthorsList() == null)
			return null;

		String authors=getCitationAuthors(reference.getArticle().getAuthorsList());

		sb.append(authors).append(" ").append("(").append(reference.getArticle().getPublishYear()).append(")");
		return sb.toString();
	}
	
	public String getAbstractText() {
		return abstractText;
	}
}
