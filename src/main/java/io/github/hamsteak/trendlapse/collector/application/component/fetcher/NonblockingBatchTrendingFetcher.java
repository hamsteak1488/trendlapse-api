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
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Primary
@Component
@RequiredArgsConstructor
public class NonblockingBatchTrendingFetcher implements TrendingFetcher {
    private final NonblockingYoutubeDataApiCaller nonblockingYoutubeDataApiCaller;

    @Override
    public List<TrendingItem> fetch(LocalDateTime dateTime, int collectSize, List<String> regionCodes, int maxResultCount) {
        return Flux.fromIterable(regionCodes)
                .flatMap(regionCode ->
                        fetchTrendings(collectSize, regionCode, maxResultCount, null)
                                .map(videoResponses -> mapFromResponsesToItems(dateTime, regionCode, videoResponses))
                                .flatMapMany(Flux::fromStream))
                .collectList()
                .block();
    }

    private Mono<Stream<VideoResponse>> fetchTrendings(int remainingCount, String regionCode, int maxResultCount, String pageToken) {
        int resultCount = Math.min(remainingCount, maxResultCount);
        Mono<TrendingListResponse> responseMono = nonblockingYoutubeDataApiCaller.fetchTrendings(resultCount, regionCode, pageToken);

        return responseMono.flatMap(trendingListResponse -> {
            Stream<VideoResponse> trendingResponses = trendingListResponse.getItems().stream();

            if (trendingListResponse.getNextPageToken() == null || remainingCount == resultCount) {
                return Mono.just(trendingResponses);
            }

            Mono<Stream<VideoResponse>> postPageResponseMono = fetchTrendings(remainingCount - resultCount, regionCode, maxResultCount, trendingListResponse.getNextPageToken());

            return Mono.just(trendingResponses).zipWith(postPageResponseMono, Stream::concat);
        });
    }

    private static Stream<TrendingItem> mapFromResponsesToItems(LocalDateTime dateTime, String regionCode, Stream<VideoResponse> videoResponses) {
        Iterator<Integer> indexCounter = IntStream.iterate(0, i -> i + 1).iterator();
        return videoResponses
                .map(videoResponse -> {
                    int rank = indexCounter.next() + 1;
                    String videoYoutubeId = videoResponse.getId();
                    return new TrendingItem(dateTime, regionCode, rank, videoYoutubeId);
                });
    }


}
