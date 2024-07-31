package com.maple.config;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.cache.Caching;
import java.util.HashMap;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager ehcacheManager() {
        CacheConfiguration<String, HashMap> cacheConfig = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, HashMap.class, ResourcePoolsBuilder.heap(4))
                // heap 메모리: JVM에서 관리하는 메모리 영역, Java 객체가 생성되고 관리됨
                // 4개의 엔트리 (공지사항, 업데이트, 이벤트, 캐시샵 공지사항) 유지
                .withExpiry(ExpiryPolicyBuilder.noExpiration())
                // 캐시가 영구히 만료되지 않도록 정책 설정
                .build();

        javax.cache.CacheManager cacheManager = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider")
                .getCacheManager();

        String cacheName = "myCache";
        cacheManager.destroyCache(cacheName);
        cacheManager.createCache(cacheName, Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfig));

        return new JCacheCacheManager(cacheManager);
    }
}
