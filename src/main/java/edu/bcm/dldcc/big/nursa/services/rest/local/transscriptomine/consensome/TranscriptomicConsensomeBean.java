package edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.consensome;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.nursa.model.transcriptomic.TransSummary_;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.transform.ResultTransformer;

import edu.bcm.dldcc.big.nursa.model.omics.dto.ConsensomeResult;
import edu.bcm.dldcc.big.nursa.model.omics.Consensome;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.TransSummary;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.ConsensomeData;

/**
 * Bean to manage TX Consensome data
 * @author mcowiti
 *
 */
@Stateless
public class TranscriptomicConsensomeBean implements Serializable {

	private static Logger log = Logger.getLogger(TranscriptomicConsensomeBean.class.getName());

	private static final long serialVersionUID = 52610131906573909L;
	
	@PersistenceContext(unitName = "NURSA")
    private EntityManager entityManager;
	
	final static String SQL_CONSENSOMESUMMARIES="select ID,PATHWAY, PHYSIOLOGICALSYSTEM, ORGAN, SPECIES, "
			+ "NUMBERDATASETS, NUMBEREXPERIMENTS, NUMBERDATAPOINTS, VERSIONDATE, "
			+ "CVERSION,DOI,TITLE  "
			+ "from CONSENSOMESUMMARY where type='Transcriptomic'";
	
	final static String SQL_CONSENSOMESUMMARY="select ID,PATHWAY, PHYSIOLOGICALSYSTEM, ORGAN, SPECIES, "
			+ "NUMBERDATASETS, NUMBEREXPERIMENTS, NUMBERDATAPOINTS, VERSIONDATE,CVERSION,DOI,TITLE "
			+ "from CONSENSOMESUMMARY  where type='Transcriptomic' "
			+ "and lower(PATHWAY)=:pw and lower(PHYSIOLOGICALSYSTEM)=:ps "
			+ "and  lower(ORGAN)=:o and lower(SPECIES)=:s";
	
	final static String SQL_CONSENSOMESUMMARY_BYDOI="select ID,PATHWAY, PHYSIOLOGICALSYSTEM, ORGAN, SPECIES, "
			+ "NUMBERDATASETS, NUMBEREXPERIMENTS, NUMBERDATAPOINTS, VERSIONDATE,CVERSION,DOI,TITLE "
			+ "from CONSENSOMESUMMARY  where type='Transcriptomic'  "
			+ "and DOI=:doi";
	
	final static String SQL_CONSENSOME_CURRENT_COUNT="select count(1) "
			+ "from CONSENSOME where "
			+ "lower(PATHWAY)=:pw and lower(PHYSIOLOGICALSYSTEM)=:ps "
			+ "and  lower(ORGAN)=:o and lower(SPECIES)=:s ";
	
	
	final static String SQL_CONSENSOME_CURRENT="select ID, PATHWAY, PHYSIOLOGICALSYSTEM, ORGAN, SPECIES, GENE, "
			+ "RANK,  LCPVALUE, -1*LCPVALUE, DISCRATE,GMFC, VERSIONDATE "
			+ "from CONSENSOME where "
			+ "lower(PATHWAY)=:pw and lower(PHYSIOLOGICALSYSTEM)=:ps "
			+ "and  lower(ORGAN)=:o and lower(SPECIES)=:s "
			+ "order by RANK";
	
	
	final static String SQL_CONSENSOMEDATA_CURRENT="select ID, PATHWAY, PHYSIOLOGICALSYSTEM, ORGAN, SPECIES, GENE, "
			+ "RANK,  LCPVALUE, -1*LCPVALUE, DISCRATE, VERSIONDATE,"
			+ "GMFC, CPVALUE, CBYFDRVALUE, NEXPTESTED "
			+ "from CONSENSOME where "
			+ "lower(PATHWAY)=:pw and lower(PHYSIOLOGICALSYSTEM)=:ps "
			+ "and  lower(ORGAN)=:o and lower(SPECIES)=:s "
			+ "order by RANK";
	
	public  List<TransSummary> findSummary(String pw,String ps,String o,String s){
		return getSummary(SQL_CONSENSOMESUMMARY,SummarySearch.pwpsos,pw,ps,o,s,null);
	}
	
	public  List<TransSummary> findSummary(String doi){
		return getSummary(SQL_CONSENSOMESUMMARY_BYDOI,SummarySearch.doi,null,null,null,null,doi);
	}
	
	public  List<TransSummary> findSummaries(){
		return getSummary(SQL_CONSENSOMESUMMARIES,SummarySearch.all,null,null,null,null,null);
	}
	
	public TransSummary findConsensomeSummary(String doi){
		
		try{
			CriteriaBuilder cb=entityManager.getCriteriaBuilder();
			CriteriaQuery<TransSummary> criteria=cb.createQuery(TransSummary.class);
			Root<TransSummary> root=criteria.from(TransSummary.class);
			//FIXME criteria.where(cb.equal(root.get(TransSummary_.doi),doi));
			criteria.select(root);
			return  entityManager.createQuery(criteria).getSingleResult();
	    }catch (NoResultException nre){
	    	return null;
	    }  	
	}
	
	
	public int countConsensomes(String doi,String pw,String ps,String o,String s){
		
		if(doi != null){
			List<TransSummary> list=this.findSummary(doi);
			if(list == null || list.size() == 0 )//unlikely
				return 0;
			
			
			TransSummary sum=list.get(0);
			return countConsensomes(sum.getKey().getFamily(),sum.getKey().getPhysiologicalSystem(),sum.getKey().getOrgan(),sum.getKey().getSpecies());
		}
		return countConsensomes(pw,ps,o,s);
	}
	
	private int countConsensomes(String pw,String ps,String o,String s){
		 StatelessSession session=null;
		 try{
		      session=(entityManager.getEntityManagerFactory().unwrap(SessionFactory.class))
		          .openStatelessSession();
		      
			      org.hibernate.Query query=session.createSQLQuery(SQL_CONSENSOME_CURRENT_COUNT);
			      addParams(query,pw,ps,o,s); 
		          List<Object> list=query.list();
				 return ((Number)list.get(0)).intValue();
		    }catch(Exception e){
		    	log.log(Level.SEVERE, "Error "+e.getMessage());
		    }finally{
		    	session=null;
		    }
		 return 0;
	}
	
	public ConsensomeResult<Consensome> findConsensomesResult(String doi,String pw,String ps,String o,String s,int max,int page){
		if(doi != null){
			List<TransSummary> list=this.findSummary(doi);
			if(list == null || list.size() == 0 )//unlikely
				return null;
			
			TransSummary summary=list.get(0);
			List<Consensome> results= findConsensomes(summary.getKey().getFamily(), summary.getKey().getPhysiologicalSystem(), 
					summary.getKey().getOrgan(), summary.getKey().getSpecies(),max,page);
			return new ConsensomeResult(summary,results);
		}else 
			return new ConsensomeResult<Consensome>(null,findConsensomes(pw, ps, o, s,max,page));
			
	}
	
	private  List<Consensome> findConsensomes(String pw,String ps,String o,String s,int max,int page){
		 
		  StatelessSession session=null;
		 List<Consensome> list=null;
		    try{
		      session=(entityManager.getEntityManagerFactory().unwrap(SessionFactory.class))
		          .openStatelessSession();
		      
		      org.hibernate.Query query=session.createSQLQuery(SQL_CONSENSOME_CURRENT);
		       addParams(query,pw,ps,o,s); 
			   
			  query.setResultTransformer(new ResultTransformer() {
		        public Object transformTuple(Object[] result, String[] aliases) {
		        	Consensome data = null;
		        	if(result != null)
		        		data= new Consensome();
		        		/* need refactor data=new Consensome(
		        			((BigDecimal)result[0]).longValue(),
		        			(String)result[1],(String)result[2],(String)result[3],(String)result[4], 
		        			(String)result[5],
		        			((Number)result[6]).doubleValue(),
		        			((Number)result[7]).doubleValue(),
		        			((Number)result[8]).doubleValue(),//negated log
		        			((Number)result[9]).doubleValue(),
		        			((Number)result[10]).doubleValue(),//gmFc
		        			(Date)result[11]
		        			);
		        	*/
		            return data;
		        }
		        public List transformList(List list) {
		            return list;
		        }
			  });
		  
			  list=query.setFirstResult(page).setMaxResults(max).list();
			 	 
		    }catch(Exception e){
		    	log.log(Level.SEVERE, "Error "+e.getMessage());
		    }finally{
		    	session=null;
		    }
		return list;
	}
	
	public  List<ConsensomeData> findConsensomeData(String pw,String ps,String o,String s,int max){
		 
		  StatelessSession session=null;
		 List<ConsensomeData> list=null;
		    try{
		      session=(entityManager.getEntityManagerFactory().unwrap(SessionFactory.class))
		          .openStatelessSession();
		      
		      org.hibernate.Query query=session.createSQLQuery(SQL_CONSENSOMEDATA_CURRENT);
		      addParams(query,pw,ps,o,s); 
		       	
			  query.setResultTransformer(new ResultTransformer() {
		        public Object transformTuple(Object[] result, String[] aliases) {

					ConsensomeData data = new ConsensomeData();
		        	/* To refactor ConsensomeData data = new ConsensomeData(
		        			((BigDecimal)result[0]).longValue(),
		        			(String)result[1],(String)result[2],(String)result[3],(String)result[4], 
		        			(String)result[5],
		        			((Number)result[6]).doubleValue(),//rank
		        			((Number)result[7]).doubleValue(), //lcp
		        			((Number)result[8]).doubleValue(),//negated log
		        			((Number)result[9]).doubleValue(), //dis
		        			(Date)result[10], 
		        			((Number)result[11]).doubleValue(), //gmfc
		        			((Number)result[12]).doubleValue(), //cPValue
		        			((Number)result[13]).doubleValue(), //cfby
		        			((Number)result[14]).intValue()  //next
		        			);
		        	*/
		            return data;
		        }
		        public List transformList(List list) {
		            return list;
		        }
			  });
		  
			  list=query.setMaxResults(max).list();
			 	 
		    }catch(Exception e){
		    	log.log(Level.SEVERE, "Error "+e.getMessage());
		    }finally{
		    	session=null;
		    }
		return list;
	}
	
	private  List<TransSummary> getSummary(String sql,SummarySearch sType,String pw,String ps,String o,String s,String doi){
		 StatelessSession session=null;
		 List<TransSummary> list=null;
		    try{
		      session=(entityManager.getEntityManagerFactory().unwrap(SessionFactory.class))
		          .openStatelessSession();
		      
		      org.hibernate.Query query=session.createSQLQuery(sql);
		      
		     switch(sType){
			     case pwpsos:
			    	 addParams(query,pw,ps,o,s); 
			    	 break;
			     case doi:
			    	 adDoiParam(query,doi); 
			    	 break;
			     case all:
			    	 default:
			     }
		        
			  query.setResultTransformer(new ResultTransformer() {
		        public Object transformTuple(Object[] result, String[] aliases) {
		        	TransSummary sumdata = null;
		        	 if(result !=null)
		        		 sumdata=new TransSummary(
		        			(String)result[1],(String)result[2],(String)result[3],(String)result[4], 
		        			((Number)result[5]).longValue(),
		        			((Number)result[6]).longValue(),
		        			((Number)result[7]).longValue(),
		        			(Date)result[8],
		        			((Number)result[9]).intValue(),//cversion
		        			(String)result[10],//doi
		        			(String)result[11]);//title
		            return sumdata;
		        }
		        public List transformList(List list) {
		            return list;
		        }
			  });
		  
			  list=query.list();
			  
		    }catch(Exception e){
		    	log.log(Level.SEVERE, "Error "+e.getMessage());
		    }finally{
		    	session=null;
		    }
		return list;
	}
	
	
	/**
	 * Add query params
	 * @param query
	 * @param pw
	 * @param ps
	 * @param o
	 * @param s
	 */
	private void addParams(org.hibernate.Query query,String pw,String ps,String o,String s){
		query.setParameter("pw", pw.toLowerCase())
        .setParameter("ps", ps.toLowerCase())
        .setParameter("o", o.toLowerCase())
        .setParameter("s", s.toLowerCase());
	}
	private void adDoiParam(org.hibernate.Query query,String doi){
		query.setParameter("doi", doi);
	}
}
