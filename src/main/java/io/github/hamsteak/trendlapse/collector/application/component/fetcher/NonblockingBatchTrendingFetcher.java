package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.NonblockingYoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class NonblockingBatchTrendingFetcher implements TrendingFetcher {
    private final NonblockingYoutubeDataApiCaller nonblockingYoutubeDataApiCaller;

    @Override
    public List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, List<String> regionCodes, int maxResultCount) {
        return Flux.fromIterable(regionCodes)
                .flatMap(regionCode ->
                        fetchRegionTrendings(collectSize, regionCode, maxResultCount, null, new ArrayList<>())
                                .map(videoResponses -> mapFromResponsesToItems(dateTime, regionCode, videoResponses))
                                .flatMapMany(Flux::fromIterable)
                )
                .collectList()
                .block();
    }

    private Mono<List<VideoResponse>> fetchRegionTrendings(int remainingCount, String regionCode, int maxResultCount, String pageToken, List<VideoResponse> trendingsAcc) {
        int resultCount = Math.min(remainingCount, maxResultCount);
        Mono<TrendingListResponse> responseMono = nonblockingYoutubeDataApiCaller.fetchTrendings(resultCount, regionCode, pageToken);

        return responseMono.flatMap(trendingListResponse -> {
            trendingsAcc.addAll(trendingListResponse.getItems());

            if (trendingListResponse.getNextPageToken() == null || remainingCount == resultCount) {
                return Mono.just(trendingsAcc);
            }

            return fetchRegionTrendings(remainingCount - resultCount, regionCode, maxResultCount, trendingListResponse.getNextPageToken(), trendingsAcc);
        });
    }

    private static List<TrendingItem> mapFromResponsesToItems(LocalDateTime dateTime, String regionCode, List<VideoResponse> videoResponses) {
        List<TrendingItem> trendingItems = new ArrayList<>();
        for (int i = 0; i < videoResponses.size(); i++) {
            int rank = i + 1;
            String videoYoutubeId = videoResponses.get(i).getId();
            trendingItems.add(new TrendingItem(dateTime, regionCode, rank, videoYoutubeId));
        }
        return trendingItems;
    }


}
