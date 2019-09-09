import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import junit.framework.Assert;

public class MemoryCache {

	/*LoadingCache<Long, String> cache = CacheBuilder.newBuilder()
		       .maximumSize(5)
		       .expireAfterWrite(5, TimeUnit.SECONDS)
		       .build( new CacheLoader<Long, String>() {
		             public String load(Long key) throws Exception {
		               return createkey(key);
		             }
		           });*/
	
	public String createkey(Long key){
		//cache.get(key)
		return "";
	}
	
	Cache<Long, String> cache = CacheBuilder.newBuilder()
		       .maximumSize(5)
		       .expireAfterWrite(5, TimeUnit.SECONDS)
		       .build();
	
	@Test
	public void test() throws Exception{
		cache.put(1L, "one");
		cache.put(2L, "two");
		cache.put(3L, "three");
		cache.put(4L, "four");
		
		Assert.assertTrue("1 Found",cache.asMap().get(1L)!= null); 
		Assert.assertTrue("3 Found",cache.asMap().get(3L)!= null); 
	}
	
	@Test
	public void testMaxSize() throws Exception{
		cache.put(1L, "one");
		cache.put(2L, "two");
		cache.put(3L, "three");
		cache.put(4L, "four");
		cache.put(5L, "five");
		cache.put(6L, "six");
		Assert.assertTrue(" 1 !Found",cache.asMap().get(1L) == null); 
	}
	
	@Test
	public void testExpire() throws Exception{
		cache.put(1L, "one");
		Thread.sleep(6000);
		Assert.assertTrue(" 1 expired",cache.asMap().get(1L) == null); 
	}
	
}
