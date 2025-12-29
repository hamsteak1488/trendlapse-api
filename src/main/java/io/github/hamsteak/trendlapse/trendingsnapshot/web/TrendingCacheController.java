package io.github.hamsteak.trendlapse.trendingsnapshot.web;

import io.github.hamsteak.trendlapse.global.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.trendlapse.global.errors.exception.RestApiException;
import io.github.hamsteak.trendlapse.trendingsnapshot.config.CacheInvalidationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trendings/cache")
public class TrendingCacheController {
    private final CacheManager cacheManager;
    private final CacheInvalidationConfig cacheInvalidationConfig;

    @PostMapping("/clear")
    public ResponseEntity<?> clearCache(@RequestParam String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER, "Cannot find cache. [cacheName: " + cacheName + "]");
        }

        cache.clear();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle-always-invalidate")
    public ResponseEntity<?> toggleAlwaysInvalidateCache(@RequestParam String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER, "Cannot find cache. [cacheName: " + cacheName + "]");
        }

        cacheInvalidationConfig.setAlwaysInvalidateCache(!cacheInvalidationConfig.isAlwaysInvalidateCache());

        return ResponseEntity.ok().build();
    }
}
