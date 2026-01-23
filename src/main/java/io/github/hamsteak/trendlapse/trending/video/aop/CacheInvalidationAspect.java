package io.github.hamsteak.trendlapse.trending.video.aop;

import io.github.hamsteak.trendlapse.trending.video.config.CacheInvalidationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class CacheInvalidationAspect {
    private final CacheManager cacheManager;
    private final CacheInvalidationConfig cacheInvalidationConfig;
    private final static String CACHE_NAME = "trendingVideoView";

    @Before("execution(* io.github.hamsteak.trendlapse.trendingsnapshot.application.SearchTrendingService.search(..))")
    public void invalidateCache(JoinPoint joinPoint) {
        if (!cacheInvalidationConfig.isAlwaysInvalidateCache()) {
            return;
        }

        Cache cache = cacheManager.getCache(CACHE_NAME);

        if (cache == null) {
            log.warn("Cannot find cache. name={}", CACHE_NAME);
            return;
        }

        cache.clear();
    }
}
