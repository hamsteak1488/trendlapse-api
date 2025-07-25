package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.trendlapse.common.errors.exception.RestApiException;
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
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND, "Cannot find trending (id:" + trendingId + ")"));
    }
}
