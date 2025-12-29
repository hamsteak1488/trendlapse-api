package io.github.hamsteak.trendlapse.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("trendingVideoView");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterAccess(Duration.ofDays(1))
                .recordStats()
        );
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
