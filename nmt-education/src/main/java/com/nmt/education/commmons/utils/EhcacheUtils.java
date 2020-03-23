package com.nmt.education.commmons.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.Objects;

/**
 * @Author: PeterChen
 * @Date: 2019/3/16 16:34
 * @Version 1.0
 */
public class EhcacheUtils {

    private static CacheManager cacheManager;

    public static void setCache(String cacheName, String key, Object value) {
        Cache cache = cacheManager().getCache(cacheName);
        Element element = new Element(key, value);
        cache.put(element);
    }

    public static Object getCache(String cacheName, String key) {
        Object object = null;
        Cache cache = cacheManager().getCache(cacheName);
        if (cache.get(key) != null) {
            object = cache.get(key).getObjectValue();
        }
        return object;
    }

    private static CacheManager cacheManager() {
        if (Objects.nonNull(cacheManager())) {
            return cacheManager;
        } else {
            cacheManager = (CacheManager) SpringContextUtil.getBean(CacheManager.class);
            return cacheManager;
        }
    }


}
