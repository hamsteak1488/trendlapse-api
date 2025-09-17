package io.github.hamsteak.trendlapse.collector.fetcher;

import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchTrendingFetcher implements TrendingFetcher {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    @Override
    public List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, String regionCode) {
        List<VideoResponse> responses = fetchTrendings(collectSize, regionCode);

        return IntStream.range(0, responses.size())
                .mapToObj(i -> {
                    int rank = i + 1;
                    String videoYoutubeId = responses.get(i).getId();
                    return new TrendingItem(dateTime, regionCode, rank, videoYoutubeId);
                })
                .toList();
    }

    private List<VideoResponse> fetchTrendings(int collectSize, String regionCode) {
        List<VideoResponse> responses = new ArrayList<>();

        String pageToken = null;
        int remainingCount = collectSize;
        while (remainingCount > 0) {
            int maxResultCount = Math.min(remainingCount, youtubeDataApiProperties.getMaxResultCount());

            TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(maxResultCount, regionCode, pageToken);
            responses.addAll(trendingListResponse.getItems());

            if (trendingListResponse.getNextPageToken() == null) {
                break;
            }
            pageToken = trendingListResponse.getNextPageToken();

            remainingCount -= youtubeDataApiProperties.getMaxResultCount();
        }

        return responses;
    }
}
