package io.github.hamsteak.trendlapse.trending.video.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CacheInvalidationConfig {
    private boolean alwaysInvalidateCache = false;
}
