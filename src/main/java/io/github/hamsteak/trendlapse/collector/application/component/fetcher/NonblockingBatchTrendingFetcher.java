package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.NonblockingYoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class NonblockingBatchTrendingFetcher implements TrendingFetcher {
    private final NonblockingYoutubeDataApiCaller nonblockingYoutubeDataApiCaller;

    @Override
    public List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, List<String> regionCodes, int maxResultCount) {
        List<TrendingItem> trendingItems = Flux.fromIterable(regionCodes)
                .flatMap(regionCode ->
                        fetchTrendings(collectSize, regionCode, maxResultCount, null)
                                .map(videoResponses -> mapFromResponsesToItems(dateTime, regionCode, videoResponses))
                                .flatMapMany(Flux::fromIterable))
                .collectList()
                .block();

        if (trendingItems == null) {
            log.warn("Trending fetch appears to have failed; list is empty. regionCodes={}", regionCodes);
            return List.of();
        }

        return trendingItems;
    }

    private Mono<List<VideoResponse>> fetchTrendings(int remainingCount, String regionCode, int maxResultCount, String pageToken) {
        int resultCount = Math.min(remainingCount, maxResultCount);
        Mono<TrendingListResponse> responseMono = nonblockingYoutubeDataApiCaller.fetchTrendings(resultCount, regionCode, pageToken);

        return responseMono.flatMap(trendingListResponse -> {
            List<VideoResponse> trendingResponses = trendingListResponse.getItems();

            if (trendingListResponse.getNextPageToken() == null || remainingCount == resultCount) {
                return Mono.just(trendingResponses);
            }

            Mono<List<VideoResponse>> postPageResponseMono
                    = fetchTrendings(remainingCount - resultCount, regionCode, maxResultCount, trendingListResponse.getNextPageToken());

            return Mono.just(trendingResponses).zipWith(postPageResponseMono,
                    (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).toList());
        });
    }

    private static List<TrendingItem> mapFromResponsesToItems(LocalDateTime dateTime, String regionCode, List<VideoResponse> videoResponses) {
        Iterator<Integer> indexCounter = IntStream.iterate(0, i -> i + 1).iterator();
        return videoResponses.stream()
                .map(videoResponse -> {
                    int rank = indexCounter.next() + 1;
                    String videoYoutubeId = videoResponse.getId();
                    return new TrendingItem(dateTime, regionCode, rank, videoYoutubeId);
                })
                .toList();
    }


}
