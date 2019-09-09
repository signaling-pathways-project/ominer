package edu.bcm.dldcc.big.nursa.services.rest.omics.cache;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;

import edu.bcm.dldcc.big.nursa.model.omics.dto.OmicsDatapoint;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.TmQueryResponse;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.persistence.leveldb.configuration.LevelDBStoreConfigurationBuilder;

import edu.bcm.dldcc.big.nursa.model.common.Article;
import edu.bcm.dldcc.big.nursa.model.omics.SignalingPathway;
import edu.bcm.dldcc.big.nursa.services.rest.consumers.PubchemData;

@Singleton
@Startup
@Lock(javax.ejb.LockType.READ)
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class ApisCacheManager {

    @Resource
    private TimerService timerService;

	private Logger log = Logger.getLogger(ApisCacheManager.class.getName());
	private DefaultCacheManager defoltCacheManager ;

	public static org.infinispan.Cache<String, TmQueryResponse<OmicsDatapoint>> queryCache ;
	public static org.infinispan.Cache<String, Article> pubmedCache ;
    public static org.infinispan.Cache<String, PubchemData> pubchemCache ;
	public static org.infinispan.Cache<String, List<SignalingPathway>> pathwaysNodesCache ;
	
	@PostConstruct
	public void setup(){
		defoltCacheManager = new DefaultCacheManager(buildCacheConfig());
		queryCache  = defoltCacheManager.getCache("pubchem-cache");
		pathwaysNodesCache  = defoltCacheManager.getCache("pubmed-cache");
		
		log.log(Level.WARNING,"started cache manager, named caches ... "+ defoltCacheManager.getCacheNames());
        log.log(Level.INFO,"Query cache size  = "+ queryCache.size());
	}
	
	
	private Configuration buildCacheConfig(){
		Configuration cacheConfig = new ConfigurationBuilder().persistence()
                .addStore(LevelDBStoreConfigurationBuilder.class)
                .location("/tmp/leveldb/data/")//("./infinispan/apiCalls/leveldb/data")//
                .expiredLocation("/tmp/leveldb/expired/")//("./infinispan/apiCalls/leveldb/expired")//
                .build();
		return cacheConfig;
	}

    @Schedule(minute = "*/30", hour = "*", info="queryCacheSizeTimer", persistent = false)
    public void getQuerySize(){
        log.log(Level.INFO,"Query cache size  = "+ queryCache.size());
    }

	@PreDestroy
	public void cleanupCacheManager(){
		try{
			if(defoltCacheManager!=null)
				defoltCacheManager.stop();
		}catch (Exception e){
			log.log(Level.SEVERE,"@cleanupCacheManager "+e.getMessage()); 
		}

        for (Timer timer : timerService.getTimers()) {
            log.log(Level.WARNING,"   cancelling scheduled timers before Bean is taken out of service");
            timer.cancel();
        }
	}
}
