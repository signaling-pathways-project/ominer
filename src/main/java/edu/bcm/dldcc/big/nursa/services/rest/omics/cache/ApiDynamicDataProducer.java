package edu.bcm.dldcc.big.nursa.services.rest.omics.cache;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.joda.time.LocalDateTime;

import edu.bcm.dldcc.big.nursa.model.Ligand;
import edu.bcm.dldcc.big.nursa.model.Ligand_;
import edu.bcm.dldcc.big.nursa.model.rest.ApiSource;
import edu.bcm.dldcc.big.nursa.model.rest.ExternalApisConfig;
import edu.bcm.dldcc.big.nursa.model.rest.ExternalApisConfig_;
import edu.bcm.dldcc.big.nursa.model.rest.GeneralConfig;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemApiProxyByClient;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemData;

/**
 * Ligand API data producer
 * This will be cached(Infinispan)/replaced by mongo documents
 * This data fronts pubchem API (only call API if not been cached ): 
 * 	there is a max of 5 call/s restrictions
 * Hash/Infinispan Cache/mongo will be refreshed on demand or via some schedule
 * @author mcowiti
 *
 */
@Singleton
@Startup
public class ApiDynamicDataProducer {

	private static final int PUBCHEM_API_FAILURE_TRY_AGAIN_MINS = 10;

	private static Logger log = Logger.getLogger(ApiDynamicDataProducer.class.getName());
	
	@PersistenceContext(unitName = "NURSA")
	private EntityManager em;
	
	//@Inject private ApisCacheManager cacheManager;
	
	@Inject
	private PubchemApiProxyByClient pubchemApiProxyByClient;
	

	@Resource
    private TimerService timerService;
	
	public static ConcurrentHashMap<ApiSource,LocalDateTime> failureLogs=new ConcurrentHashMap<ApiSource,LocalDateTime>();
	private LocalDateTime lastFailureLog; 
	
	@PostConstruct
	public void setup(){
		log.log(Level.INFO, "dynamic remote(API) data producer started ");
		//may pre-fetch  Ligands,config here, if will update Entities on changes
	}
	
	@PreDestroy
	public void removeTimers(){
	     for (Timer timer : timerService.getTimers()) {
	          log.log(Level.WARNING,"   cancelling scheduled timers before bean is taken out of service");
	          timer.cancel();
	      }
	 }
	
	private boolean refreshScheduleNoted=false;
	private boolean refreshRunning=false;
	/**
	 * Refresh pubchem data, and (optionally) cache
	 * If saving to mongo, do so at night (18-20)
	 * 1 record in 0.5s, max 5/sec requests
	 * at setting about 1/1sec => 60/min=>3600/hr
	 */
	@Lock(javax.ejb.LockType.READ)
	//FIXME Need work logic of clearing cache, rebuilding
	//@Schedule(second="*/30", minute = "*", hour = "18-20", info="pubchemDataRefreshTimer", persistent = false)
	public void refreshPubchemData(){
		
		if(!refreshScheduleNoted)
			log.log(Level.INFO, "dynamic remote(API) refresh started ");
		
		refreshScheduleNoted=true;
		
		
		ExternalApisConfig pubchemConfig=getExternalApisConfig(ApiSource.pubchem);
		if(pubchemConfig == null)
			return;
		
		if(!pubchemConfig.isRefresh())
			return;
		
		if(toReTryPostFailure(ApiSource.pubchem))
			return;
		
		List<Ligand> ligands=getLigandsWithCid();
		int count=0;
		int errorCount=0;
		PubchemData record;
		this.refreshRunning=true;
		for(Ligand ligand:ligands){
			if(count++ >10)
				break;
			if(ligand.getPubchemId() == null)
				continue;
			
			ApisCacheManager.pubchemCache.clear();
			try{
				record=updateCache(ligand.getPubchemId().toString());
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error seeking pubchem:  with "+e.getMessage()+" for CID: " +ligand.getPubchemId());
				if(++errorCount < 3)
					continue;
				failureLogs.put(ApiSource.pubchem, LocalDateTime.now());
				e.printStackTrace();
				break;
			}
			refreshLocalDatum(ligand,record);
		}
		this.refreshRunning=false;
	}
	
	private boolean pubchemCacheScheduleNoted=false;
	private boolean pubchemCacheRunning=false;
	/**
	 * run on schedule, getting API results. 
	 * one reason why need cache? since Molecules->Search->display list shows ligand desc, cannot dynamically get all these on list showing
	 * respect 5calls/sec
	 */
	@Lock(javax.ejb.LockType.READ)
	//@Schedule(second="*/20", minute = "*", hour = "*", info="pubchemCacheTimer", persistent = false)
	public void populatePubchemCache(){
		
		if(this.refreshRunning)
			return;
		
		if(pubchemCacheRunning)
			return;
		
		if(!pubchemCacheScheduleNoted){
			log.log(Level.INFO, "Starting pubchem cache builder ");
			log.log(Level.WARNING, "Molecule/Ligand pages removed, so quiting cache building ");
			pubchemCacheScheduleNoted=true;
			 for (Timer timer : timerService.getTimers()) {
				 if(timer.getInfo().equals("pubchemCacheTimer")){
					 log.log(Level.WARNING, "pubchemCacheTimer cancelled ");
					 timer.cancel();
				 }
			 }
			return;
		}
		
		pubchemCacheScheduleNoted=true;
		pubchemCacheRunning=true;
		if(!toReTryPostFailure(ApiSource.pubchem)){
			log.log(Level.INFO, "3 prior pubchem API call failures, will retry after(m): "+PUBCHEM_API_FAILURE_TRY_AGAIN_MINS);
			return;
		}
		
		List<Ligand> ligands=getLigandsWithCid();
		if(ligands.size() == ApisCacheManager.pubchemCache.size())
			return;
		
		//cases where #cids!=cache size
		if(allCidsCached(ligands))
			return;
		
		log.log(Level.FINE, "Pubchem cache: #ligands.cid -> #@cache "+ligands.size()+"->"+ApisCacheManager.pubchemCache.size());
		
		int count=0;
		int errorCount=0;
		PubchemData record;
		for(Ligand ligand:ligands){
			if(ApisCacheManager.pubchemCache.containsKey(ligand.getPubchemId().toString()))
				continue;
			if(count >10)
				break;
			if(ligand.getPubchemId() == null)
				continue;
			
			try {
				record=updateCache(ligand.getPubchemId().toString());
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error seeking pubchem:  with "+e.getMessage()+" for CID: " +ligand.getPubchemId());
				if(++errorCount <3)
					continue;
				failureLogs.put(ApiSource.pubchem, LocalDateTime.now());
				e.printStackTrace();
				break;
			}
			
			refreshLocalDatum(ligand,record);
			count++;
		}
		pubchemCacheRunning=false;
	}
	
	//cases where there are cid duplicates, #cache!=#ligands size
	private boolean allCidsCached(List<Ligand> ligands){
		for(Ligand ligand:ligands){
			if(ApisCacheManager.pubchemCache.containsKey(ligand.getPubchemId().toString()))
				continue;
			else
				return false;
		}
		return true;
	}
	
	/**
	 * Refresh locally persisted datum
	 * These data are used in application logic, eg public /api/ queries
	 * @param ligand
	 * @param record
	 */
	private void refreshLocalDatum(Ligand ligand,PubchemData record){
		if(ligand.getPubchemId() != null)
			if(ligand.getLastPubchemUpdate() == null){
				if(record != null){
					//TODO noFR show 1st choice, detail will show all
					if(record.getDescriptions() !=null && record.getDescriptions().size() > 0) {
						ligand.setBlurb(getDescriptions(record.getDescriptions()));
					}
					
					//these needed by /api/ for Query
					if(ligand.getCasId() == null && record.getCas() != null)
						ligand.setCasId(record.getCas());
					if(ligand.getChebi() == null && record.getChebi() != null)
						ligand.setChebi(record.getChebi());
					if(ligand.getIuphar() == null && record.getIuphar() != null)
						ligand.setIuphar(record.getIuphar());
					
					if (ligand != null && ligand.getBlurb() != null && ligand.getBlurb().length() > 3900) {
						ligand.setBlurb(ligand.getBlurb().substring(0, 3900));
					}

					ligand=em.merge(ligand);
				}
			}
	}
	
	private String getDescriptions(List<String> list) {
		int count = list.size();
		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (String sD : list) {
			sb.append(sD);
			if (++i< count)
				sb.append("<hr>");
		}
		return sb.toString().replaceAll("<a([^<]*)>", " ").replaceAll("</a>", " ");
	}
	
	private boolean toReTryPostFailure(ApiSource key){
		LocalDateTime then = LocalDateTime.now().minusMinutes(PUBCHEM_API_FAILURE_TRY_AGAIN_MINS);
		boolean toMakeCall=true;
		if(!failureLogs.containsKey(key)){
			return true;//failureLogs.put(key,LocalDateTime.now());
		}else{
			lastFailureLog=failureLogs.get(key);
			toMakeCall=lastFailureLog.isBefore(then);
			if(toMakeCall)
				failureLogs.put(key,LocalDateTime.now());
		}
		return toMakeCall;
	}
	
	@Lock(javax.ejb.LockType.READ)
	public PubchemData getPubchemLigand(String pubchemId) throws Exception{
		if(ApisCacheManager.pubchemCache.containsKey(pubchemId))
			return ApisCacheManager.pubchemCache.get(pubchemId);
		return updateCache(pubchemId);
	}
	
	 @Lock(javax.ejb.LockType.READ)
	 @AccessTimeout(value=15, unit=java.util.concurrent.TimeUnit.SECONDS)
	public void onConfigChanged(@Observes @GeneralConfig ExternalApisConfig config){
			//log.info("data type config changed");
	}
	
	private PubchemData updateCache(String pubchemId) throws Exception{
		PubchemData record=pubchemApiProxyByClient.getPubchemRecordViaGson(pubchemId);
		if(record != null) 
			ApisCacheManager.pubchemCache.put(pubchemId, record);
		return record;
	}
	
	
	private List<Ligand> getLigandsWithCid(){
		CriteriaBuilder builder = this.em.getCriteriaBuilder();
		  CriteriaQuery<Ligand> criteria =builder.createQuery(Ligand.class);
		  Root<Ligand> root = criteria.from(Ligand.class);
		  TypedQuery<Ligand> query =
	        this.em.createQuery(criteria.select(root)
	        		.where(builder.isNotNull(root.get(Ligand_.pubchemId))));
		  
		  return query.
				  setHint("org.hibernate.cacheable", true)
				  .getResultList();
		 
	}
	private List<ExternalApisConfig> getExternalApisConfigs(){
		CriteriaBuilder builder = this.em.getCriteriaBuilder();
		  CriteriaQuery<ExternalApisConfig> criteria =builder.createQuery(ExternalApisConfig.class);
		  Root<ExternalApisConfig> root = criteria.from(ExternalApisConfig.class);
		  TypedQuery<ExternalApisConfig> query =
	        this.em.createQuery(criteria.select(root));
		  
		  return query.
				  setHint("org.hibernate.cacheable", true)
				  .getResultList();
		 
	}
	
	private ExternalApisConfig getExternalApisConfig(ApiSource type){
		CriteriaBuilder builder = this.em.getCriteriaBuilder();
		  CriteriaQuery<ExternalApisConfig> criteria =builder.createQuery(ExternalApisConfig.class);
		  Root<ExternalApisConfig> root = criteria.from(ExternalApisConfig.class);
		  TypedQuery<ExternalApisConfig> query =
	        this.em.createQuery(criteria.select(root).where(
		    		builder.equal(root.get(ExternalApisConfig_.apiSource),type )));
		  //builder.equal(root.<String> get("apiSource"),type )));
		  try{
		  return query.
				  setHint("org.hibernate.cacheable", true)
				 .getSingleResult();
		  }catch (Exception e){
			  return null;
		  }
	}
}
