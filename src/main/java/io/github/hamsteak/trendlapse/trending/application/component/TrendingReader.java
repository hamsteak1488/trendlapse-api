package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.global.errors.exception.TrendingNotFoundException;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TrendingReader {
    private final TrendingRepository trendingRepository;

    @Transactional(readOnly = true)
    public Trending read(long trendingId) {
        return trendingRepository.findById(trendingId)
                .orElseThrow(() -> new TrendingNotFoundException("Cannot find trending (id:" + trendingId + ")"));
    }
}
