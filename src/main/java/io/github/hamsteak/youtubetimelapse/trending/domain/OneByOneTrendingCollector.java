package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.RestTemplateYoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Trending 목록 조회 -> Trending 하나씩 삽입 (Trending 만드는데 Video 없다면 생성 (Video 만드는데 Channel 없다면 생성) )
 * API 호출 횟수: Trending(1) + Video(N) + Channel(N)
 * DB 쿼리 횟수: Trending(insert:N) + Video(select:N + insert:N) + Channel(select:N + insert:N)
 */

//@Primary
@Component
@RequiredArgsConstructor
public class OneByOneTrendingCollector implements TrendingCollector {
    private final RestTemplateYoutubeDataApiCaller trendingApiCaller;
    private final TrendingPutter trendingPutter;

    @Override
    public void collect(LocalDateTime dateTime, int collectCount) {
        List<VideoResponse> responses = trendingApiCaller.fetchTrendings(collectCount).stream()
                .flatMap(response -> response.getItems().stream())
                .toList();

        IntStream.range(0, responses.size())
                .forEach(i -> trendingPutter.put(dateTime, responses.get(i).getId(), i + 1));
    }
}
