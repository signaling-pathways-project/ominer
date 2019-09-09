package edu.bcm.dldcc.big.nursa.services.rest.omics.cache;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.ejb.Timer;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.nursa.model.Molecule;
import edu.bcm.dldcc.big.nursa.model.Molecule_;
import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.PathwayNodeDataSummary;
import edu.bcm.dldcc.big.nursa.services.FamilyNodeCache;
import edu.bcm.dldcc.big.nursa.services.rest.omics.BsmServicebean;
import edu.bcm.dldcc.big.nursa.services.rest.omics.PathwayServiceBean;
import edu.bcm.dldcc.big.nursa.services.rest.omics.QueryType;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import edu.bcm.dldcc.big.nursa.model.cistromic.PathwayNodesView;
import edu.bcm.dldcc.big.nursa.services.rest.omics.BsmNode;

/**
 * Map cache for lookups
 * @author mcowiti
 *
 */
@Singleton
@Startup
@Lock(javax.ejb.LockType.READ)
public class PathwaysNodesMapBean implements Serializable {

	private static Logger log = Logger.getLogger(PathwaysNodesMapBean.class.getName());

	private static final long serialVersionUID = 4680852424717403871L;
	
	@PersistenceContext(unitName = "NURSA")
    private EntityManager em;

	private final static String SQL_NODE_CATEGORY_SUMMARY =
			"select catid as nodeId, dtype as type, count(unique expid) as count from dataset_view where dtype !='Metabolomic' group by catid,dtype";

	@Inject
	private PathwayServiceBean pathwayServiceBean;

	@Resource
	private TimerService timerService;

    @Inject
    private BsmServicebean bsmServicebean;


    public static ConcurrentHashMap<Long,List<String>> familyIdNodeMap=new ConcurrentHashMap<Long,List<String>>();
	public static ConcurrentHashMap<Long,String> modelIdNodeMap=new ConcurrentHashMap<Long,String>();

	private static ConcurrentHashMap<Long,List<String>> familyIdNodeMapTemp=new ConcurrentHashMap<Long,List<String>>();

	public static ConcurrentHashMap<String,List<String>> bsmToNodesMap=new ConcurrentHashMap<String,List<String>>();
	private static ConcurrentHashMap<String,List<String>> bsmToNodesMapTemp=new ConcurrentHashMap<String,List<String>>();

    public static ConcurrentHashMap<String,List<String>> bsmToNodesInactiveMap=new ConcurrentHashMap<String,List<String>>();

	public static ConcurrentHashMap<String,Long> speciesMap=new ConcurrentHashMap<String,Long>();
	public static List<SignalingPathway> allPathways=new ArrayList<SignalingPathway>();
    public static List<SignalingPathway> allModelsPathways=new ArrayList<SignalingPathway>();

    public static ConcurrentHashMap<Long,Integer> txCategoryCountMap=new ConcurrentHashMap<Long,Integer>();
	public static ConcurrentHashMap<Long,Integer> cisCategoryCountMap=new ConcurrentHashMap<Long,Integer>();

    public static ConcurrentHashMap<String,String> directionsMap=new ConcurrentHashMap<String,String>();
    public static ConcurrentHashMap<String,String> signalingPathwayNodeTypesMap=new ConcurrentHashMap<String,String>();

    public static ConcurrentHashMap<Long,String> psIdsMap=new ConcurrentHashMap<Long,String>();
    public static ConcurrentHashMap<Long,String> organIdsMap=new ConcurrentHashMap<Long,String>();


    private boolean setup=false;
	private boolean pathwaysCached=false;

	// //{"Signaling Pathway Modules", "Animal & Cell Models","Diseases"};
	public static final String TOP_LEVEL_SIG_PATH_MODULES = "Signaling Pathway Modules";

    private String[] humanMatches = new String[] {"Human", "Hs","9606","13"};
    private String[] mouseMatches = new String[] {"House Mouse","Mouse", "Mm","10090","16"};
    private String[] ratMatches = new String[] {"Norway Rat","Rat", "Rn","10116","17"};

    private String[] directions = new String[] {"any", "up","down"};
    private String[] sigPathNodesTypes = new String[] {"type","domain", "category","class","family"};

    @PostConstruct
	public void setup(){
		long b=System.currentTimeMillis();
		updates();
		log.log(Level.INFO, " Cache #bsmToNodesMap/familyIdNodeMap/time(ms)"+bsmToNodesMap.size()+"/"+familyIdNodeMap.size()+"/"+(System.currentTimeMillis()-b));
		log.log(Level.INFO, " Pathway Nodes usage summary #cis/#tx category/EG Cis Cat counts/Tx counts "+cisCategoryCountMap.size()+"/"+txCategoryCountMap.size()+" / "+cisCategoryCountMap.toString()+" / "+txCategoryCountMap.toString());
        log.log(Level.INFO, " PS/Organ counts "+psIdsMap.size()+"/"+organIdsMap.size());
        log.log(Level.INFO, " ModelIds counts "+modelIdNodeMap.size());
    }

	private void updates(){
		setupPathIdNodeMap(false);
		setupBsmNodeMap(false);
		setupSimpleMaps();
		setupCategoryUsageNodeCount();
        setupInactiveBsmToNodes();
        setupPsAndOrganMap();
        setupModelsIdmap();
	}

	@Schedule(minute = "*/3", hour = "*", info="pathwaysCacheSetupTimer", persistent = false)
	public void cachePathways(){
		
		long b=System.currentTimeMillis();
		if(!ApisCacheManager.pathwaysNodesCache.containsKey("root")){
			log.log(Level.WARNING, "Building new allPathways cache ...");
			allPathways= this.pathwayServiceBean.getSignalingPathways(PathwayCategory.type);
			initPathwayExpCountByCategory();
			ApisCacheManager.pathwaysNodesCache.put("root", allPathways);
			log.log(Level.WARNING, "Building new allPathways cache done!");
			if((System.currentTimeMillis()-b) > 5000)
				log.log(Level.SEVERE, "#allPathways query time(ms)"+allPathways.size()+"/"+(System.currentTimeMillis()-b));

		}else{
			log.log(Level.INFO, "Using existing allPathways cache ...");
			allPathways=ApisCacheManager.pathwaysNodesCache.get("root");
			initPathwayExpCountByCategory();
			log.log(Level.INFO, "allPathways cache setup");
		}
		
		for (Timer timer : timerService.getTimers()) {
	        if(timer.getInfo().equals("pathwaysCacheSetupTimer"))  
	        	timer.cancel();
	      }
	}


    @Schedule(minute = "*/5", hour = "*", info="InactiveBsmNodeMappingsTimer", persistent = false)
	public void checkInactiveBsmNodeMappings(){

        setupInactiveBsmToNodes();

        for (Timer timer : timerService.getTimers()) {
            if(timer.getInfo().equals("InactiveBsmNodeMappingsTimer"))
				timer.cancel();
		}
	}


	@PreDestroy
	public void removeTimers(){
		for (Timer timer : timerService.getTimers()) {
			log.log(Level.WARNING,"   cancelling scheduled timers before bean is taken out of service");
			timer.cancel();
		}
	}

	private void updatePathwaysCache(){
		if(pathwaysCached)
			return;
		
		pathwaysCached=true;
		allPathways= this.pathwayServiceBean.getSignalingPathways(PathwayCategory.type);
		initPathwayExpCountByCategory();
		ApisCacheManager.pathwaysNodesCache.put("root", allPathways);
		pathwaysCached=false;
	}

    private void setupInactiveBsmToNodes(){
        List<BsmInactiveView> list=bsmServicebean.getInactiveBsmNodeMappings();

        Map<String, List<BsmInactiveView>> viewByBsm
                = list.stream().collect(Collectors.groupingBy(BsmInactiveView::getBsm));

        for(Map.Entry<String, List<BsmInactiveView>> entry:viewByBsm.entrySet()) {
            bsmToNodesInactiveMap.put(entry.getKey(), entry.getValue().stream().map(e -> e.getNode()).collect(Collectors.toList()));
        }
    }

	private void initPathwayExpCountByCategory(){
		Optional<SignalingPathway> path;
		for(Map.Entry<Long, Integer> entry:txCategoryCountMap.entrySet()) {
			path= getSignalingPathwayCategoryById(entry.getKey(),allPathways);
			if(path.isPresent()){
				path.get().setCisExpCount(cisCategoryCountMap.get(entry.getKey()));
				path.get().setTransExpCount(entry.getValue());
			}
		}
	}

	private Optional<SignalingPathway> getSignalingPathwayCategoryById(Long id, List<SignalingPathway> pathways){

		for (SignalingPathway path : pathways) {
			if(path.getType() == PathwayCategory.type)
				return getSignalingPathwayCategoryById(id,path.getPathways());

			if (path.getId().equals(id))
				return Optional.of(path);
		}
		return Optional.empty();
	}

	//HGNC symbols do not change often, but mappings may change
	public void updatesPathwayMaps(){
		if(setup)
			return;
		setup=true;
		updates();
		setup=false;
	}

	@Asynchronous
	@AccessTimeout(value=15,unit=java.util.concurrent.TimeUnit.SECONDS)
	public void onFamilyChanged(@Observes @FamilyNodeCache PathwayNodesView familyNode) {
		long b=System.currentTimeMillis();
		log.log(Level.WARNING,"updating caches (familyIdNodeMap,bsmToNodesMap,NodeUsageCount,allPathways)...");

		setupPathIdNodeMap(true);
		setupBsmNodeMap(true);

		familyIdNodeMap.clear();
		familyIdNodeMap.putAll(familyIdNodeMapTemp);

		bsmToNodesMap.clear();
		bsmToNodesMap.putAll(bsmToNodesMapTemp);

		familyIdNodeMapTemp.clear();
		bsmToNodesMapTemp.clear();

		setupCategoryUsageNodeCount();
		updatePathwaysCache();

        setupPsAndOrganMap();

		log.log(Level.WARNING,"updating caches done (ms)"+(System.currentTimeMillis()-b));
	}

	/**
	 * Need get from DB to allow dynamic
	 */
	private void setupSimpleMaps(){
		speciesMap.put("Human", 13L);
		speciesMap.put("House Mouse", 16L);
		speciesMap.put("Norway Rat", 17L);
		setCommontSpeciesTerms();
        setupDirectionsMap();
        setupSigPathNodesTypesMap();
	}

	public static Map<String,Long> speciesCommonTerms = new HashMap<String,Long>();


	private void setupDirectionsMap(){
        for (String s : directions)
            directionsMap.put(s.toLowerCase(),s);
    }

    private void setupSigPathNodesTypesMap(){
        for (String s : sigPathNodesTypes)
            signalingPathwayNodeTypesMap.put(s.toLowerCase(),s);
    }
	private void setCommontSpeciesTerms(){
			for (String s : humanMatches)
				speciesCommonTerms.put(s.toLowerCase(),13L);

			for (String m : mouseMatches)
				speciesCommonTerms.put(m.toLowerCase(),16L);

			for (String r : ratMatches)
				speciesCommonTerms.put(r.toLowerCase(),17L);
	}

	private void setupBsmNodeMap(boolean rehash){
        setBsmNodeMapBySQL(rehash);
	}

    private void setBsmNodeMapBySQL(boolean rehash){

        List<BsmNode> list=bsmServicebean.getBsmViewBySQL();

        Map<String, List<BsmNode>> viewByBsm
                = list.stream().collect(Collectors.groupingBy(BsmNode::getBsm));

        for(Map.Entry<String, List<BsmNode>> entry:viewByBsm.entrySet()){
            if(rehash)
                bsmToNodesMapTemp.put(entry.getKey(), entry.getValue().stream().map(e->e.getNode()).collect(Collectors.toList()));
            else
                bsmToNodesMap.put(entry.getKey(), entry.getValue().stream().map(e->e.getNode()).collect(Collectors.toList()));
        }
    }


	private void setupCategoryUsageNodeCount(){
		org.hibernate.Query q = null;
		q = (org.hibernate.Query) (em.unwrap(Session.class)).createSQLQuery(SQL_NODE_CATEGORY_SUMMARY);
		((SQLQuery) q).addScalar("nodeId", StandardBasicTypes.LONG);
		((SQLQuery) q).addScalar("type", StandardBasicTypes.STRING);
		((SQLQuery) q).addScalar("count", StandardBasicTypes.INTEGER);

		q.setResultTransformer(Transformers.aliasToBean(PathwayNodeDataSummary.class));
		List<PathwayNodeDataSummary> list= (List<PathwayNodeDataSummary>) q.list();

		Map<String, List<PathwayNodeDataSummary>> viewByOmicsType
				= list.stream().collect(Collectors.groupingBy(PathwayNodeDataSummary::getType));

		String qtype=null;
		for(Map.Entry<String, List<PathwayNodeDataSummary>> entry:viewByOmicsType.entrySet()){
			qtype=entry.getKey();
			if(QueryType.valueOf(qtype) == QueryType.Cistromic) {
				for(PathwayNodeDataSummary sum:entry.getValue())
					cisCategoryCountMap.put(sum.getNodeId(),sum.getCount());
			}else {
				for(PathwayNodeDataSummary sum:entry.getValue())
					txCategoryCountMap.put(sum.getNodeId(),sum.getCount());
			}
		}
	}
	
	private void setupPathIdNodeMap(boolean rehash){
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PathwayNodesView> criteria = cb.createQuery(PathwayNodesView.class);
		Root<PathwayNodesView> pathRoot = criteria.from(PathwayNodesView.class);
		criteria.select(pathRoot);
		List<PathwayNodesView> list= em.createQuery(criteria).getResultList();

		Map<Long, List<PathwayNodesView>> viewByFamily
				= list.stream().collect(Collectors.groupingBy(PathwayNodesView::getFamilyid));

		for(Map.Entry<Long, List<PathwayNodesView>> entry:viewByFamily.entrySet()){
			if(rehash)
				this.familyIdNodeMapTemp.put(entry.getKey(), entry.getValue().stream().map(e->e.getNode()).collect(Collectors.toList()));
			else
				this.familyIdNodeMap.put(entry.getKey(), entry.getValue().stream().map(e->e.getNode()).collect(Collectors.toList()));
		}
	}

	private void setupModelsIdmap(){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Molecule> criteria = cb.createQuery(Molecule.class);
        Root<Molecule> root = criteria.from(Molecule.class);
        criteria.select(root).where(cb.equal(root.get(Molecule_.type),"Other"));

        List<Molecule> list=em.createQuery(criteria).getResultList();
        modelIdNodeMap.putAll(list.stream().collect(
                Collectors.toMap(Molecule::getId, Molecule::getOfficialSymbol)));
    }

	private void setupPsAndOrganMap() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TissueCategory> criteria = cb.createQuery(TissueCategory.class);
        Root<TissueCategory> pathRoot = criteria.from(TissueCategory.class);
        criteria.select(pathRoot);
        List<TissueCategory> list = em.createQuery(criteria).getResultList();

        Map<Boolean, List<TissueCategory>> groups = list.stream()
                .collect(Collectors.partitioningBy(s -> (s.getParent() == null)));
        List<TissueCategory> parents = groups.get(true);
        List<TissueCategory> organs = groups.get(false);

        psIdsMap.putAll((Map<Long, String>)parents.stream().collect(Collectors.toMap(TissueCategory::getId, TissueCategory::getName)));
        organIdsMap.putAll((Map<Long, String>)organs.stream().collect(Collectors.toMap(TissueCategory::getId, TissueCategory::getName)));
    }
}