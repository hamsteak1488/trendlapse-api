package io.github.hamsteak.trendlapse.trending.domain;

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

import java.util.Arrays;
import java.util.List;

@Slf4j
@Order(1)
@Aspect
@Component
@RequiredArgsConstructor
public class TrendingCacheLoggingAspect {
    private final CacheManager cacheManager;

    @Before("execution(* io.github.hamsteak.trendlapse.trending.domain.CacheDateTimeTrendingDetailListFinder.find(..)) && @annotation(cacheable)")
    public void logTrendingCache(JoinPoint joinPoint, Cacheable cacheable) {
        String cacheName = cacheable.value()[0];
        List<String> stringArgs = Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList().subList(0, 2);
        String key = String.join(":", stringArgs);

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("Failed to find cache. (cacheName={})", cacheName);
            return;
        }

        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper == null) {
            log.info("[Cache Miss] name={}, key={}", cacheName, key);
        } else {
            log.info("[Cache Hit] name={}, key={}", cacheName, key);

        }
    }
}
