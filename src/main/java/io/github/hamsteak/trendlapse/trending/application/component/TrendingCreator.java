package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.trending.application.dto.TrendingCreateDto;

import java.util.List;

public interface TrendingCreator {
    int create(List<TrendingCreateDto> trendingCreateDtos);
}
