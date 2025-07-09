package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.RestTemplateYoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class TrendingCollectorV1 implements TrendingCollector {
    private final RestTemplateYoutubeDataApiCaller trendingApiCaller;
    private final TrendingPutter trendingPutter;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount) {
        List<VideoResponse> responses = trendingApiCaller.getTrendings(collectCount).stream()
                .flatMap(response -> response.getItems().stream())
                .toList();

        IntStream.range(0, responses.size())
                .forEach(i -> trendingPutter.put(dateTime, responses.get(i).getId(), i + 1));
    }
}
