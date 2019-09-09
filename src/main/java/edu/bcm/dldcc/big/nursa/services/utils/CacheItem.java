package edu.bcm.dldcc.big.nursa.services.utils;

/**
 * Created by alexey on 4/14/15.
 */
public class CacheItem {
    public Long createTime;
    public Long maxAge;
    public Object obj;

    public boolean isExpired()
    {
        return (System.currentTimeMillis() - createTime) >= maxAge;
    }

    public CacheItem(Object obj, Long maxAge)
    {
        this.createTime = System.currentTimeMillis();
        this.obj = obj;
        this.maxAge = maxAge;
    }
}
