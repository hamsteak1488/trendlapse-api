package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.RestTemplateYoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.config.Constants;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class TrendingCollector {
    private final RestTemplateYoutubeDataApiCaller trendingApiCaller;
    private final TrendingPutter trendingPutter;
    private final int collectCount;

    public TrendingCollector(RestTemplateYoutubeDataApiCaller trendingApiCaller, TrendingPutter trendingPutter, @Value("${collect-count}") int collectCount) {
        this.trendingApiCaller = trendingApiCaller;
        this.trendingPutter = trendingPutter;
        this.collectCount = collectCount;
    }

    @Scheduled(fixedDelay = Constants.COLLECT_INTERVAL)
    public void collect() {
        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        List<VideoResponse> responses = trendingApiCaller.getTrendings(collectCount).stream()
                .flatMap(response -> response.getItems().stream())
                .toList();

        IntStream.range(0, responses.size())
                .forEach(i -> trendingPutter.put(dateTime, responses.get(i).getId(), i + 1));
    }
}
