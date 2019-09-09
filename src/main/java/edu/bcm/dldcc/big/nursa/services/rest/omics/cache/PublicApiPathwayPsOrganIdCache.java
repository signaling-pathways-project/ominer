package edu.bcm.dldcc.big.nursa.services.rest.omics.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.nursa.model.common.Species;
import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway_;
import edu.bcm.dldcc.big.nursa.model.omics.TissueCategory;


/**
 * Pathway,PS/Organ lookups  cache for API id lookup
 * No support for newer topLevel Pathway
 * Existing TM Query uses lookup Ids.
 * @author mcowiti
 *
 */
@Singleton
@Lock(javax.ejb.LockType.READ)
public class PublicApiPathwayPsOrganIdCache {

	private static Logger log = Logger.getLogger(PublicApiPathwayPsOrganIdCache.class.getName());
	
	public static ConcurrentHashMap<String,String> parentPathways=new ConcurrentHashMap<String,String>();
	public static ConcurrentHashMap<String,Map<String,String>> childrenPathways=new ConcurrentHashMap<String,Map<String,String>>();
	public static ConcurrentHashMap<String,String> pathIds=new ConcurrentHashMap<String,String>();
	
	public static ConcurrentHashMap<String,String> parentTissues=new ConcurrentHashMap<String,String>();
	public static ConcurrentHashMap<String,Map<String,String>> childrenTissues=new ConcurrentHashMap<String,Map<String,String>>();
	public static ConcurrentHashMap<String,String> tissueIds=new ConcurrentHashMap<String,String>();
	
	public static ConcurrentHashMap<String,String> speciesNames=new ConcurrentHashMap<String,String>();
	
	private int totals=0;
	private int tissueTotals=0;
	
	@PersistenceContext(unitName = "NURSA")
    private EntityManager em;
	
	boolean run=false;
	
	//@PostConstruct
	//4now TODO @Schedule(minute = "*/5", hour = "*", info="PathwayPsOrganIdCacheSetup", persistent = false)
	public void setup(){
		if(run)
			return;
		run=true;
		setupPathways();
		setupTissue();
		setupSpecies();
	}
	
	 @Resource
	   private TimerService timerService;
	
	@PreDestroy
	public void removeTimers(){
	     for (Timer timer : timerService.getTimers()) {
	          log.log(Level.WARNING,"   cancelling scheduled timers before bean is taken out of service");
	          timer.cancel();
	      }
	 }
	
	//4now TODO @Schedule(minute = "*/15", hour = "*", info="pathwayCacheUpdatesTimer", persistent = false)
	public void reCheckUpdates(){
		if(this.getPathwaysCount() > this.totals)
			setupPathways();
		if(this.getPsOrganCounts() > this.tissueTotals)
			setupTissue();
	}
	
	private void setupPathways(){
		List<SignalingPathway> list=getPathways(null);
		
		this.totals=getPathwaysCount();
		
		//need add spn/class/family
		
		Map<Boolean, List<SignalingPathway>> groups = list.stream()
 		        .collect(Collectors.partitioningBy(s -> (s.getType() == PathwayCategory.category)));
 	    List<SignalingPathway> parents = groups.get(true);
 	   
 	    parentPathways.clear();
 	    childrenPathways.clear();
 	    pathIds.clear();
 	    
 	   pathIds=new ConcurrentHashMap<String, String>(listToIds(list));
 	    parentPathways= new ConcurrentHashMap<String, String>(listToIdMap(parents));
 	    for(SignalingPathway parent:parents)
 	    	childrenPathways.put(parent.getName(), listToIdMap(parent.getPathways()));
 	    
 	    log.log(Level.INFO,"#total->#parentPaths->#allPaths="+totals+"->"+parentPathways.size()+"->"+pathIds.size());
	}
	
	private void setupTissue(){
		
		List<TissueCategory> list=this.getPsAndOrgans();
		
		this.tissueTotals=getPsOrganCounts();
		
		Map<Boolean, List<TissueCategory>> groups = list.stream()
 		        .collect(Collectors.partitioningBy(s -> (s.getParent() == null)));
 	    List<TissueCategory> parents = groups.get(true);
 	   List<TissueCategory> children = groups.get(false);
 	   
 	    parentTissues.clear();
 	    childrenTissues.clear();
 	    tissueIds.clear();
 	    
 	   tissueIds=new ConcurrentHashMap<String, String>(listToTissueIds(list));
 	   parentTissues= new ConcurrentHashMap<String, String>(listToTissueIdMap(parents));
 	    for(TissueCategory parent:parents)
 	    	childrenTissues.put(parent.getName(), listToTissueIdMap(getMyChildren(children,parent)));
 	   
 	    log.log(Level.INFO,"#total->#PS->#Organs->#all Categories="+totals+"->"+parentTissues.size()+"->"+childrenTissues.size()+"->"+tissueIds.size());
	}
	
	private void setupSpecies(){
		List<Species> list=this.getSpecies();
		Map<String, String> map=(Map<String, String>) list.stream().filter(u->u.getCommonName() != null)
				  .collect(Collectors.toMap(Species::getIdentifier, Species::getCommonName));
		speciesNames= new ConcurrentHashMap<String,String> (map);
		
		 log.log(Level.INFO,"#Species-="+
			Collections.list(PublicApiPathwayPsOrganIdCache.speciesNames.keys())
			.stream()
			.collect(Collectors.joining(", ")));
	}
	
	private List<TissueCategory> getMyChildren(List<TissueCategory> allChildren,TissueCategory parent){
		return  allChildren.stream().filter(u->u.getParent() == parent).collect(Collectors.toList());	
	}
	
	
	
	private Map<String,String> listToIdMap(List<SignalingPathway> list){
		return (Map<String, String>) list.stream().filter(u->u != null)
				  .collect(Collectors.toMap(SignalingPathway::getName, SignalingPathway::getIdentifier));	
	}
	private Map<String,String> listToTissueIdMap(List<TissueCategory> list){
		try{
			return (Map<String, String>) list.stream().filter(u->u != null)
				  .collect(Collectors.toMap(TissueCategory::getName, TissueCategory::getIdentifier));	
		}catch (Exception e){
			log.log(Level.SEVERE,"Failed read Tissues",e);
			return new HashMap<String,String>();
		}
	}
	private Map<String, String> listToIds(List<SignalingPathway> list){
		return  (Map<String, String>)list.stream().collect(Collectors.toMap(SignalingPathway::getIdentifier, SignalingPathway::getIdentifier));
	}
	private Map<String, String> listToTissueIds(List<TissueCategory> list){
		return  (Map<String, String>)list.stream().collect(Collectors.toMap(TissueCategory::getIdentifier, TissueCategory::getIdentifier));
	}
	
	public List<SignalingPathway> getPathways(PathwayCategory type) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SignalingPathway> criteria = cb.createQuery(SignalingPathway.class);
		Root<SignalingPathway> pathRoot = criteria.from(SignalingPathway.class);
		criteria.select(pathRoot);
		if(type!=null)
			criteria.where(cb.equal(pathRoot.get(SignalingPathway_.type),type));
		return em.createQuery(criteria).getResultList();
	}
	public Integer getPathwaysCount() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<SignalingPathway> pathRoot = criteria.from(SignalingPathway.class);
		criteria.select(cb.count(pathRoot));
		return ((Number)em.createQuery(criteria).getSingleResult()).intValue();
	}
	
	public List<Species> getSpecies() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Species> criteria = cb.createQuery(Species.class);
		Root<Species> root = criteria.from(Species.class);
		criteria.select(root);
		return em.createQuery(criteria).getResultList();
	}
	
	public List<TissueCategory> getPsAndOrgans() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TissueCategory> criteria = cb.createQuery(TissueCategory.class);
		Root<TissueCategory> pathRoot = criteria.from(TissueCategory.class);
		criteria.select(pathRoot);
		return em.createQuery(criteria).getResultList();
	}
	public Integer getPsOrganCounts() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<TissueCategory> pathRoot = criteria.from(TissueCategory.class);
		criteria.select(cb.count(pathRoot));
		return ((Number)em.createQuery(criteria).getSingleResult()).intValue();
	}
}
