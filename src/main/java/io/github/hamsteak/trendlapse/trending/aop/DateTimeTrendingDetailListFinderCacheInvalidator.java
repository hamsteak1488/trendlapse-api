package io.github.hamsteak.trendlapse.trending.aop;

import io.github.hamsteak.trendlapse.trending.domain.CacheInvalidationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DateTimeTrendingDetailListFinderCacheInvalidator {
    private final CacheManager cacheManager;
    private final CacheInvalidationConfig cacheInvalidationConfig;

    @Before("execution(* io.github.hamsteak.trendlapse.trending.domain.CacheByDayDateTimeTrendingDetailListFinder.find(..)) && @annotation(cacheable)")
    public void invalidateCache(JoinPoint joinPoint, Cacheable cacheable) {
        if (!cacheInvalidationConfig.isAlwaysInvalidateCache()) {
            return;
        }

        String cacheName = cacheable.value()[0];
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            log.warn("Cannot find cache. name={}", cacheName);
            return;
        }

        cache.clear();
    }
}
