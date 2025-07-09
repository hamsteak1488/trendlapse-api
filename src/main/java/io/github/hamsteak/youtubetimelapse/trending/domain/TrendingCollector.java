package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.RestTemplateYoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.config.Constants;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TrendingCollector {
    private final RestTemplateYoutubeDataApiCaller trendingApiCaller;
    private final TrendingPutter trendingPutter;

    @Scheduled(fixedDelay = Constants.COLLECT_INTERVAL)
    public void collect() {
        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        VideoListResponse trendingResponses = trendingApiCaller.getTrendings();

        IntStream.range(0, trendingResponses.getItems().size())
                .forEach(i -> trendingPutter.put(dateTime, trendingResponses.getItems().get(i).getId(), i + 1));
    }
}
