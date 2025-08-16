package io.github.hamsteak.trendlapse.collector.domain.v1;

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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchVideoCollector implements VideoCollector {
    private final BatchChannelCollector batchChannelCollector;
    private final VideoFinder videoFinder;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final VideoCreator videoCreator;

    public int collect(List<String> videoYoutubeIds) {
        videoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds.stream().distinct().toList());

        List<VideoResponse> videoResponses = fetchVideos(videoYoutubeIds);

        List<String> channelYoutubeIds = videoResponses.stream().map(VideoResponse::getChannelYoutubeId).distinct().toList();
        batchChannelCollector.collect(channelYoutubeIds);

        return storeFromResponses(videoResponses);
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
            log.info("The length of the list of videos to fetch and the length of the list of videos in response are different. diff={}", diff);
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
                log.info("Cannot find channel despite channel collection tasks. (videoYoutubeId={}, channelYoutubeId={})", videoYoutubeId, channelYoutubeId);
            }
        }

        return storedCount;
    }
}
