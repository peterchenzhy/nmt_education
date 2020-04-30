package com.nmt.education.config.cache;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @author Stephen.Shi
 * @version v1
 * @summary CacheManager配置
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @since 2018/9/9
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class CacheManagerConfig {
    private final CacheProperties cacheProperties;

    CacheManagerConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    /**
     * cacheManager名字
     */
    public interface CacheManagerNames {
        /**
         * redis
         */
        String REDIS_CACHE_MANAGER = "redisCacheManager";
        /**
         * ehCache
         */
        String EHCACHE_CACHE_MANAGER = "ehCacheCacheManager";
    }

//    /**
//     * 缓存名，名称暗示了缓存时长 注意： 如果添加了新的缓存名，需要同时在下面的RedisCacheCustomizer#RedisCacheCustomizer里配置名称对应的缓存时长
//     * ，时长为0代表永不过期；缓存名最好公司内部唯一，因为可能多个项目共用一个redis。
//     *
//     * @see RedisCacheCustomizer#customize(RedisCacheManager)
//     */
//    public interface CacheNames {
//        /**
//         * 15分钟缓存组
//         */
//        String CACHE_15MINS = "nmt_education:cache:15m";
//        /**
//         * 30分钟缓存组
//         */
//        String CACHE_30MINS = "nmt_education:cache:30m";
//        /**
//         * 60分钟缓存组
//         */
//        String CACHE_60MINS = "nmt_education:cache:60m";
//        /**
//         * 180分钟缓存组
//         */
//        String CACHE_180MINS = "nmt_education:cache:180m";
//    }

    /**
     * ehcache缓存名
     */
    public interface EhCacheNames {
        String CACHE_10MINS = "nmt_education:cache:10m";

    }

//    @Bean
//    public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
//        RedisTemplate redisTemplate = new RedisTemplate();
//        redisTemplate.setConnectionFactory(jedisConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }
//
//    @Bean("redisWithgenericToStringSerializer")
//    public RedisTemplate redisWithgenericToStringSerializer(JedisConnectionFactory jedisConnectionFactory) {
//        // 1.创建 redisTemplate 模版
//        RedisTemplate<Object, Object> template = new RedisTemplate<>();
//        // 2.关联 redisConnectionFactory
//        template.setConnectionFactory(jedisConnectionFactory);
//        // 3.创建 序列化类
//        GenericToStringSerializer genericToStringSerializer = new GenericToStringSerializer(Object.class);
//        // 6.序列化类，对象映射设置
//        // 7.设置 value 的转化格式和 key 的转化格式
//        template.setValueSerializer(genericToStringSerializer);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.afterPropertiesSet();
//        return template;
//    }
//
//    /**
//     * 默认的cacheManager
//     *
//     * @param redisTemplate
//     * @return
//     */
//    @Bean
//    @Primary
//    public RedisCacheManager redisCacheManager(RedisTemplate<Object, Object> redisTemplate) {
//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        cacheManager.setUsePrefix(true);
//        this.redisCacheManagerCustomizer().customize(cacheManager);
//        return cacheManager;
//    }
//
//    /**
//     * cache的一些自定义配置
//     */
//    @Bean
//    public RedisCacheCustomizer redisCacheManagerCustomizer() {
//        return new RedisCacheCustomizer();
//    }
//
//    private static class RedisCacheCustomizer
//            implements CacheManagerCustomizer<RedisCacheManager> {
//        /**
//         * CacheManager缓存自定义初始化比较早，尽量不要@autowired 其他spring 组件
//         */
//        @Override
//        public void customize(RedisCacheManager cacheManager) {
//            // 自定义缓存名对应的过期时间
//            Map<String, Long> expires = ImmutableMap.<String, Long>builder()
//                    .put(CacheNames.CACHE_15MINS, TimeUnit.MINUTES.toSeconds(15))
//                    .put(CacheNames.CACHE_30MINS, TimeUnit.MINUTES.toSeconds(30))
//                    .put(CacheNames.CACHE_60MINS, TimeUnit.MINUTES.toSeconds(60))
//                    .put(CacheNames.CACHE_180MINS, TimeUnit.MINUTES.toSeconds(180)).build();
//            // spring cache是根据cache name查找缓存过期时长的，如果找不到，则使用默认值
//            cacheManager.setDefaultExpiration(TimeUnit.MINUTES.toSeconds(30));
//            cacheManager.setExpires(expires);
//        }
//    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        Resource location = this.cacheProperties
                .resolveConfigLocation(this.cacheProperties.getEhcache().getConfig());
        return new EhCacheCacheManager(EhCacheManagerUtils.buildCacheManager(location));
    }
}
