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

/**
 * Spring Cache 설정 클래스
 */
@Configuration
@EnableCaching
public class CacheConfig {
    /**
     * Ehcache 오픈 소스 라이브러리 기반의 CacheManager를 정의하는 메서드
     * - JVM에서 관리되는 힙 메모리에 4개의 엔트리를 저장하도록 설정
     * - 캐시 항목은 영구히 만료되지 않도록 설정
     * @return JCacheCacheManager 객체
     */
    @Bean
    public CacheManager ehcacheManager() {
        CacheConfiguration<String, HashMap> cacheConfig = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, HashMap.class, ResourcePoolsBuilder.heap(4))
                .withExpiry(ExpiryPolicyBuilder.noExpiration())
                .build();

        javax.cache.CacheManager cacheManager = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider")
                .getCacheManager();

        cacheManager.destroyCache("myCache");
        cacheManager.createCache("myCache", Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfig));

        return new JCacheCacheManager(cacheManager);
    }
}
