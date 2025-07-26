package io.github.hamsteak.trendlapse.collector.config;

import io.github.hamsteak.trendlapse.collector.domain.BatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.BatchVideoCollector;
import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectorConfig {
    /*@Bean
    public TrendingCollector trendingCollector(RegionReader regionReader,
                                               YoutubeDataApiCaller youtubeDataApiCaller,
                                               YoutubeDataApiProperties youtubeDataApiProperties,
                                               TrendingPutter trendingPutter) {
        return new OneByOneTrendingCollector(regionReader, youtubeDataApiCaller, youtubeDataApiProperties, trendingPutter);
    }*/

    @Bean
    public TrendingCollector trendingCollector(RegionReader regionReader,
                                               YoutubeDataApiCaller youtubeDataApiCaller,
                                               YoutubeDataApiProperties youtubeDataApiProperties,
                                               BatchVideoCollector batchVideoCollector,
                                               TrendingCreator trendingCreator,
                                               VideoReader videoReader) {
        return new BatchTrendingCollector(regionReader, youtubeDataApiCaller, youtubeDataApiProperties, batchVideoCollector, trendingCreator, videoReader);
    }
}
