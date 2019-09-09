package edu.bcm.dldcc.big.nursa.services.rest.consumers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.infinispan.manager.DefaultCacheManager;

import org.infinispan.manager.EmbeddedCacheManager;
import org.joda.time.LocalDateTime;

import edu.bcm.dldcc.big.nursa.model.rest.ApiSource;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.TranscriptomineQueryServiceRestApi;

/**
 * A Go Term results in many genes.
 * Seems GoTerm Query need activator first time to be performant
 * TODO: use memory cache of GoTerms, return max 1000
 * @author mcowiti
 *
 */
//FIXME need??/ 4/2018 @Singleton @Lock(javax.ejb.LockType.READ)
public class GoTermActivator {

	private Logger log = Logger.getLogger(GoTermActivator.class.getName());
	public static ConcurrentHashMap<String,List<Integer>> goTerms=new ConcurrentHashMap<String,List<Integer>>();
	
	@PersistenceContext(unitName = "NURSA")
    private EntityManager entityManager;
	
	@Resource
    private TimerService timerService;
	
	private boolean active=false;
	
	//@Schedule(minute = "*/5", hour = "*", info="goTermActivatorTimer", persistent = false)
	public void preActivator(){
		
		if(this.active)
			return;
		
		this.active=true;
		log.log(Level.INFO,"building goTerms cache...");
		long b=System.currentTimeMillis();
		List<Object[]> list=getTerms();
		log.log(Level.INFO,"got goTerms #"+list.size());
		cache(list);
		
		log.log(Level.INFO,"goTerms cache done "+(System.currentTimeMillis()-b));
		for (Timer timer : timerService.getTimers()) {
	        if(timer.getInfo().equals("goTermActivatorTimer")) {
	        	timer.cancel();
	        	log.log(Level.INFO,"goTermActivatorTimer cancelled ");
	        }
	     }
		this.active=false;
	}
	
	private void preFetch(){
		TranscriptomineQueryServiceRestApi api= new TranscriptomineQueryServiceRestApi();
		String geneSearchType="goTerm";
		String goTerm="C zone";
		String findCount="1";
		Response resp=api.getDatapoints(geneSearchType, null, null, 
				null, null, null, null, null, null, null, null, null, null,null, goTerm, null, null, null, findCount);
		
	}
	
	
	public List<Object[]> getTerms(){
		
		String sql="select termname,genes_id from gotermname_geneid";
		
		/* StatelessSession session=null;
		    session=(entityManager.getEntityManagerFactory().unwrap(SessionFactory.class))
		          .openStatelessSession();*/
		    
		org.hibernate.Query q = (org.hibernate.Query) (entityManager.unwrap(Session.class))
				.createSQLQuery(sql);
		List<Object[]> list= (List<Object[]>)q.setMaxResults(5000).list();
		return list;	
	}
	
	private void cache(List<Object[]> list){
		Integer geneid;
		String term;
		 List<Integer> genes;
		for(Object[] obj:list){
			 term=((String)obj[0]);  
			 geneid=((Integer)obj[1]); 
			 if(goTerms.contains(term))
				 goTerms.get(term).add(geneid);
			 else{
				 genes= new ArrayList<Integer>();
				 genes.add(geneid);
				 goTerms.put(term,genes);
			 }
		}	
	}
}
