package edu.bcm.dldcc.big.nursa.services.rest.omics.cache;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import edu.bcm.dldcc.big.nursa.model.omics.QueryForm;

/**
 * Cache Query in case user needs to download a file
 * Use Expire cache after 6hrs.
 * Important to  sychn with browser cache
 * @author mcowiti
 *
 */
@Singleton
@Startup
@ApplicationScoped
public class FileDownloadMemoryCache implements Serializable {


	Cache<String, Object> cache = CacheBuilder.newBuilder()
		       .maximumSize(1000)
		       .expireAfterWrite(6, TimeUnit.HOURS)
		       .build();
	
	public <T> T getQueryForm(String id){
		return (T)cache.asMap().get(id);
	}
	
	@Lock(javax.ejb.LockType.WRITE)
	public <T> String cache(T form){
		String id=getFormIdString();
		cache.put(id, form);
		return id;
	}
	
	private String getFormIdString(){
		String id=new StringBuilder(generateString(new Random(),
				"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZZ123456789", 10))
				.toString();
		if(!cache.asMap().containsKey(id))
			return id;
		return getFormIdString();
	}
	public static String generateString(Random rng, String characters,
			int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}
}
