package io.github.hamsteak.trendlapse.collector.infrastructure;

import io.github.hamsteak.trendlapse.collector.application.YoutubeApiFetcher;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedChannel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedRegion;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedVideo;
import io.github.hamsteak.trendlapse.youtube.application.NonblockingYoutubeApiClient;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Primary
@Component
@Slf4j
public class NonblockingYoutubeApiFetcher implements YoutubeApiFetcher {
    private final NonblockingYoutubeApiClient nonblockingYoutubeApiClient;
    private final CollectSchedulerProperties collectSchedulerProperties;
    private final int maxResultCount;

    public NonblockingYoutubeApiFetcher(
            NonblockingYoutubeApiClient nonblockingYoutubeApiClient,
            YoutubeDataApiProperties youtubeDataApiProperties,
            CollectSchedulerProperties collectSchedulerProperties
    ) {
        this.nonblockingYoutubeApiClient = nonblockingYoutubeApiClient;
        this.collectSchedulerProperties = collectSchedulerProperties;
        this.maxResultCount = youtubeDataApiProperties.getMaxResultCount();
    }

    @Override
    public List<FetchedRegion> fetchRegions() {
        RawRegionListResponse rawRegionListResponse = nonblockingYoutubeApiClient.fetchRegions().block();

        Objects.requireNonNull(rawRegionListResponse, "Fetched region list must not be null.");

        return rawRegionListResponse.getItems().stream()
                .map(rawRegion -> new FetchedRegion(rawRegion.getId(), rawRegion.getName()))
                .toList();
    }

    @Override
    public List<FetchedChannel> fetchChannels(List<String> channelYoutubeIds) {
        List<RawChannel> rawChannels = fetchRawData(
                channelYoutubeIds,
                nonblockingYoutubeApiClient::fetchChannels,
                RawChannelListResponse::getItems
        );

        if (rawChannels == null) {
            log.warn("Fetching channels job appears to have failed.");
            return List.of();
        }

        if (rawChannels.size() != channelYoutubeIds.size()) {
            List<String> channelYoutubeIdsFromItems = rawChannels.stream().map(RawChannel::getYoutubeId).toList();
            List<String> diff = channelYoutubeIds.stream().filter(channelYoutubeId -> !channelYoutubeIdsFromItems.contains(channelYoutubeId)).toList();

            if (diff.size() < 10) {
                log.info("Expected {} channels, but only {} returned. Difference: {}",
                        channelYoutubeIds.size(), channelYoutubeIdsFromItems.size(), String.join(", ", diff));
            } else {
                log.info("Expected {} channels, but only {} returned. Difference: [{}, ...]",
                        channelYoutubeIds.size(), channelYoutubeIdsFromItems.size(), String.join(", ", diff.subList(0, 10)));
            }
        }

        return rawChannels.stream()
                .map(rawChannel ->
                        new FetchedChannel(
                                rawChannel.getYoutubeId(),
                                rawChannel.getTitle(),
                                rawChannel.getThumbnailUrl()
                        ))
                .toList();
    }

    @Override
    public List<FetchedVideo> fetchVideos(List<String> videoYoutubeIds) {
        List<RawVideo> rawVideos = fetchRawData(
                videoYoutubeIds,
                nonblockingYoutubeApiClient::fetchVideos,
                RawVideoListResponse::getItems
        );

        if (rawVideos == null) {
            log.warn("Fetching videos job appears to have failed.");
            return List.of();
        }

        if (rawVideos.size() != videoYoutubeIds.size()) {
            List<String> videoYoutubeIdsFromItems = rawVideos.stream().map(RawVideo::getYoutubeId).toList();
            List<String> diff = videoYoutubeIds.stream().filter(videoYoutubeId -> !videoYoutubeIdsFromItems.contains(videoYoutubeId)).toList();

            if (diff.size() < 10) {
                log.info("Expected {} videos, but only {} returned. Difference: [{}]",
                        videoYoutubeIds.size(), videoYoutubeIdsFromItems.size(), String.join(", ", diff));
            } else {
                log.info("Expected {} videos, but only {} returned. Difference: [{}, ...]",
                        videoYoutubeIds.size(), videoYoutubeIdsFromItems.size(), String.join(", ", diff.subList(0, 10)));
            }
        }

        return rawVideos.stream()
                .map(rawVideo ->
                        new FetchedVideo(
                                rawVideo.getYoutubeId(),
                                rawVideo.getChannelYoutubeId(),
                                rawVideo.getTitle(),
                                rawVideo.getThumbnailUrl(),
                                rawVideo.getViewCount(),
                                rawVideo.getLikeCount(),
                                rawVideo.getCommentCount()
                        ))
                .toList();
    }

    private <D, L> List<D> fetchRawData(
            List<String> dataYotubeIds,
            Function<List<String>, Mono<L>> rawDataFetcher,
            Function<L, List<D>> rawDataExtractor
    ) {
        Mono<List<D>> rawDataMono = Mono.just(List.of());

        int startIndex = 0;
        while (startIndex < dataYotubeIds.size()) {
            int endIndex = Math.min(startIndex + maxResultCount, dataYotubeIds.size());
            List<String> dataYotubeIdsToFetch = dataYotubeIds.subList(startIndex, endIndex);

            Mono<L> rawDataListResponseMono = rawDataFetcher.apply(dataYotubeIdsToFetch);

            rawDataMono = rawDataMono.zipWith(
                    rawDataListResponseMono.flatMap(listResponse ->
                            Mono.just(rawDataExtractor.apply(listResponse))),
                    (rawData, fetchedRawData) ->
                            Stream.concat(rawData.stream(), fetchedRawData.stream()).toList()
            );

            startIndex += maxResultCount;
        }

        return rawDataMono.block();
    }

    @Override
    public Map<String, List<FetchedVideo>> fetchTrendingVideos(List<String> regionIds) {
        Map<String, List<FetchedVideo>> regionFetchedVideosMap = Flux.fromIterable(regionIds)
                .flatMap(regionCode ->
                        Mono.zip(
                                Mono.just(regionCode),
                                fetchRawTrendingsVideos(
                                        regionCode,
                                        null,
                                        collectSchedulerProperties.getCollectSize()
                                ).map(rawVideos -> rawVideos.stream()
                                        .map(rawVideo ->
                                                new FetchedVideo(
                                                        rawVideo.getYoutubeId(),
                                                        rawVideo.getChannelYoutubeId(),
                                                        rawVideo.getTitle(),
                                                        rawVideo.getThumbnailUrl(),
                                                        rawVideo.getViewCount(),
                                                        rawVideo.getLikeCount(),
                                                        rawVideo.getCommentCount()
                                                )
                                        ).toList()
                                )
                        )
                ).collectMap(
                        tuple -> tuple.getT1(),
                        tuple -> tuple.getT2()
                ).block();

        if (regionFetchedVideosMap == null) {
            log.warn("Fetching trending videos job appears to have failed. regionCodes={}", regionIds);
            return Map.of();
        }

        return regionFetchedVideosMap;
    }

    private Mono<List<RawVideo>> fetchRawTrendingsVideos(String regionCode, String pageToken, int remainingCount) {
        int resultCount = Math.min(remainingCount, maxResultCount);
        Mono<RawVideoListResponse> responseMono = nonblockingYoutubeApiClient.fetchTrendings(regionCode, pageToken, resultCount);

        return responseMono.flatMap(rawVideoListResponse -> {
            List<RawVideo> rawVideos = rawVideoListResponse.getItems();

            if (rawVideoListResponse.getNextPageToken() == null || remainingCount == resultCount) {
                return Mono.just(rawVideos);
            }

            Mono<List<RawVideo>> postPageResponseMono = fetchRawTrendingsVideos(
                    regionCode,
                    rawVideoListResponse.getNextPageToken(),
                    remainingCount - resultCount
            );

            return Mono.just(rawVideos).zipWith(
                    postPageResponseMono,
                    (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).toList()
            );
        });
    }
}
