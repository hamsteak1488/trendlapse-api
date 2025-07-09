package io.github.hamsteak.youtubetimelapse.trending.config;

import io.github.hamsteak.youtubetimelapse.external.youtube.RestTemplateYoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.trending.domain.TrendingCollector;
import io.github.hamsteak.youtubetimelapse.trending.domain.TrendingCollectorV1;
import io.github.hamsteak.youtubetimelapse.trending.domain.TrendingPutter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TrendingCollectorConfig {
    @Bean
    public TrendingCollector trendingCollector(RestTemplateYoutubeDataApiCaller trendingApiCaller, TrendingPutter trendingPutter) {
        return new TrendingCollectorV1(trendingApiCaller, trendingPutter);
    }
}
