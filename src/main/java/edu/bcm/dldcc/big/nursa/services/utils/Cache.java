package edu.bcm.dldcc.big.nursa.services.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexey on 4/14/15.
 */
public class Cache {

    private final static Map<String,CacheItem> MY_CACHE = new HashMap<String, CacheItem>();
    public final static Long H24 = 24L*60*60*1000;
    public final static Long WEEK = 7*H24;

    public static Object lookUp(String key)
    {
        CacheItem item = MY_CACHE.get(key);
        if (null != item)
        {
            if (!item.isExpired())
                return item.obj;
            else
                MY_CACHE.remove(key);
        }
        return null;
    }

    public static void put (String key, Object obj, Long age)
    {
        MY_CACHE.put(key, new CacheItem(obj, age));
    }

    public static void put (String key, Object obj) {
        put(key, obj, WEEK);
    }
}
