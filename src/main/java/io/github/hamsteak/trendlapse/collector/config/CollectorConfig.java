package io.github.hamsteak.trendlapse.collector.config;

import io.github.hamsteak.trendlapse.collector.domain.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
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
                                               TrendingCreator trendingCreator,
                                               OneByOneVideoCollector oneByOneVideoCollector) {
        return new OneByOneTrendingCollector(regionReader, youtubeDataApiCaller, youtubeDataApiProperties, trendingCreator, oneByOneVideoCollector);
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
