package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.VideoResponse;
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
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    @Override
    public List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, List<String> regionCodes, int maxResultCount) {
        List<TrendingItem> trendingItems = new ArrayList<>();
        for (String regionCode : regionCodes) {
            List<VideoResponse> responses = fetchTrendings(collectSize, regionCode, maxResultCount);
            trendingItems.addAll(IntStream.range(0, responses.size())
                    .mapToObj(i -> {
                        int rank = i + 1;
                        String videoYoutubeId = responses.get(i).getId();
                        return new TrendingItem(dateTime, regionCode, rank, videoYoutubeId);
                    })
                    .toList());
        }

        return trendingItems;
    }

    private List<VideoResponse> fetchTrendings(int collectSize, String regionCode, int maxResultCount) {
        List<VideoResponse> responses = new ArrayList<>();

        String pageToken = null;
        int remainingCount = collectSize;
        while (remainingCount > 0) {
            TrendingListResponse trendingListResponse = youtubeDataApiCaller.fetchTrendings(Math.min(remainingCount, maxResultCount), regionCode, pageToken);
            responses.addAll(trendingListResponse.getItems());

            if (trendingListResponse.getNextPageToken() == null) {
                break;
            }
            pageToken = trendingListResponse.getNextPageToken();

            remainingCount -= maxResultCount;
        }

        return responses;
    }
}
