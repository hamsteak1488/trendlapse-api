package io.github.hamsteak.trendlapse.trending.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trendings/cache")
public class TrendingCacheController {
    private final CacheManager cacheManager;
    private final CacheInvalidationConfig cacheInvalidationConfig;

    @PostMapping("/clear")
    public ResponseEntity<?> clearCache() {
        Cache cache = cacheManager.getCache("trendingsByDay");

        if (cache == null) {
            return ResponseEntity.badRequest().build();
        }

        cache.clear();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle-always-invalidate")
    public ResponseEntity<?> setCacheSize() {
        Cache cache = cacheManager.getCache("trendingsByDay");

        if (cache == null) {
            return ResponseEntity.badRequest().build();
        }

        cacheInvalidationConfig.setAlwaysInvalidateCache(!cacheInvalidationConfig.isAlwaysInvalidateCache());

        return ResponseEntity.ok().build();
    }
}
