package io.github.hamsteak.trendlapse.collector.infrastructure;

import io.github.hamsteak.trendlapse.collector.application.YoutubeApiFetcher;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedChannel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedRegion;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedVideo;
import io.github.hamsteak.trendlapse.youtube.application.YoutubeApiClient;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawChannel;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawVideo;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawVideoListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BlockingYoutubeApiFetcher implements YoutubeApiFetcher {
    private final YoutubeApiClient youtubeApiClient;
    private final CollectSchedulerProperties collectSchedulerProperties;
    private final int maxResultCount;

    public BlockingYoutubeApiFetcher(
            YoutubeApiClient youtubeApiClient,
            CollectSchedulerProperties collectSchedulerProperties,
            YoutubeDataApiProperties youtubeDataApiProperties
    ) {
        this.youtubeApiClient = youtubeApiClient;
        this.collectSchedulerProperties = collectSchedulerProperties;
        this.maxResultCount = youtubeDataApiProperties.getMaxResultCount();
    }

    @Override
    public List<FetchedRegion> fetchRegions() {
        return youtubeApiClient.fetchRegions().getItems().stream()
                .map(rawRegion -> new FetchedRegion(rawRegion.getId(), rawRegion.getName()))
                .toList();
    }

    @Override
    public List<FetchedChannel> fetchChannels(List<String> channelYoutubeIds) {
        List<RawChannel> rawChannels = new ArrayList<>();

        int startIndex = 0;
        while (startIndex < channelYoutubeIds.size()) {
            int endIndex = Math.min(startIndex + maxResultCount, channelYoutubeIds.size());
            List<String> subFetchChannelYoutubeIds = channelYoutubeIds.subList(startIndex, endIndex);

            RawChannelListResponse rawChannelListResponse = youtubeApiClient.fetchChannels(subFetchChannelYoutubeIds);
            rawChannels.addAll(rawChannelListResponse.getItems());

            startIndex += maxResultCount;
        }

        if (rawChannels.size() != channelYoutubeIds.size()) {
            List<String> channelYoutubeIdsFromItems = rawChannels.stream().map(RawChannel::getYoutubeId).toList();
            List<String> diff = channelYoutubeIds.stream().filter(channelYoutubeId -> !channelYoutubeIdsFromItems.contains(channelYoutubeId)).toList();
            log.info("Expected {} channels, but only {} returned. Difference: {}", channelYoutubeIds.size(), channelYoutubeIdsFromItems.size(), diff);
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
        List<RawVideo> rawVideos = new ArrayList<>();

        int startIndex = 0;
        while (startIndex < videoYoutubeIds.size()) {
            int endIndex = Math.min(startIndex + maxResultCount, videoYoutubeIds.size());
            List<String> subFetchVideoYoutubeIds = videoYoutubeIds.subList(startIndex, endIndex);

            RawVideoListResponse rawVideoListResponse = youtubeApiClient.fetchVideos(subFetchVideoYoutubeIds);
            rawVideos.addAll(rawVideoListResponse.getItems());

            startIndex += maxResultCount;
        }

        if (rawVideos.size() != videoYoutubeIds.size()) {
            List<String> videoYoutubeIdsFromItems = rawVideos.stream().map(RawVideo::getYoutubeId).toList();
            List<String> diff = videoYoutubeIds.stream().filter(videoYoutubeId -> !videoYoutubeIdsFromItems.contains(videoYoutubeId)).toList();
            log.info("Expected {} videos, but only {} returned. Difference: {}", videoYoutubeIds.size(), videoYoutubeIdsFromItems.size(), diff);
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

    @Override
    public Map<String, List<FetchedVideo>> fetchTrendingVideos(List<String> regionIds) {
        Map<String, List<FetchedVideo>> regionFetchedVideoMap = new HashMap<>();

        for (String regionCode : regionIds) {
            List<RawVideo> rawVideos = new ArrayList<>();

            String pageToken = null;
            int remainingCount = collectSchedulerProperties.getCollectSize();
            while (remainingCount > 0) {
                RawVideoListResponse rawVideoListResponse =
                        youtubeApiClient.fetchTrendings(regionCode, pageToken, Math.min(remainingCount, maxResultCount));

                rawVideos.addAll(rawVideoListResponse.getItems());

                if (rawVideoListResponse.getNextPageToken() == null) {
                    break;
                }

                pageToken = rawVideoListResponse.getNextPageToken();

                remainingCount -= maxResultCount;
            }

            List<FetchedVideo> fetchedVideos = rawVideos.stream()
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

            regionFetchedVideoMap.put(regionCode, fetchedVideos);
        }

        return regionFetchedVideoMap;
    }
}
