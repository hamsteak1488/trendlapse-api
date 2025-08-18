package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.collector.domain.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.VideoCollector;
import io.github.hamsteak.trendlapse.common.errors.exception.ChannelNotFoundException;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.VideoCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "video-strategy", havingValue = "batch", matchIfMissing = true)
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchVideoCollector implements VideoCollector {
    private final ChannelCollector channelCollector;
    private final VideoFinder videoFinder;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final VideoCreator videoCreator;

    public int collect(List<String> videoYoutubeIds) {
        videoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds.stream().distinct().toList());

        log.info("Found {} missing videos.", videoYoutubeIds.size());

        List<VideoResponse> videoResponses = fetchVideos(videoYoutubeIds);

        List<String> channelYoutubeIds = videoResponses.stream().map(VideoResponse::getChannelYoutubeId).distinct().toList();
        channelCollector.collect(channelYoutubeIds);

        int storedCount = storeFromResponses(videoResponses);

        log.info("Stored {} videos.", storedCount);

        return storedCount;
    }

    private List<VideoResponse> fetchVideos(List<String> videoYoutubeIds) {
        List<VideoResponse> responses = new ArrayList<>();

        int startIndex = 0;
        while (startIndex < videoYoutubeIds.size()) {
            int endIndex = Math.min(startIndex + youtubeDataApiProperties.getMaxResultCount(), videoYoutubeIds.size());
            List<String> subFetchVideoYoutubeIds = videoYoutubeIds.subList(startIndex, endIndex);

            VideoListResponse videoListResponse = youtubeDataApiCaller.fetchVideos(subFetchVideoYoutubeIds);
            responses.addAll(videoListResponse.getItems());

            startIndex += youtubeDataApiProperties.getMaxResultCount();
        }

        if (responses.size() != videoYoutubeIds.size()) {
            List<String> videoYoutubeIdsInResponses = responses.stream().map(VideoResponse::getId).toList();
            List<String> diff = videoYoutubeIds.stream().filter(videoYoutubeId -> !videoYoutubeIdsInResponses.contains(videoYoutubeId)).toList();
            log.info("Expected {} videos, but only {} returned. Difference: {}", videoYoutubeIds.size(), videoYoutubeIdsInResponses.size(), diff);
        }

        return responses;
    }

    private int storeFromResponses(List<VideoResponse> videoResponses) {
        int storedCount = 0;

        for (VideoResponse videoResponse : videoResponses) {
            String videoYoutubeId = videoResponse.getId();
            String channelYoutubeId = videoResponse.getSnippet().getChannelId();

            try {
                videoCreator.create(
                        videoYoutubeId,
                        channelYoutubeId,
                        videoResponse.getSnippet().getTitle(),
                        videoResponse.getSnippet().getThumbnails().getHigh().getUrl()
                );
                storedCount++;
            } catch (ChannelNotFoundException ex) {
                log.info("Skipping video record creation: No matching channel found (videoYoutubeId={}, channelYoutubeId={}).",
                        videoYoutubeId, channelYoutubeId);
            }
        }

        return storedCount;
    }
}
