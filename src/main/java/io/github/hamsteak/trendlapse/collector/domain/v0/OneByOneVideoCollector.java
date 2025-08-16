package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.collector.domain.VideoCollector;
import io.github.hamsteak.trendlapse.common.errors.exception.ChannelNotFoundException;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.video.domain.VideoCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OneByOneVideoCollector implements VideoCollector {
    private final OneByOneChannelCollector oneByOneChannelCollector;
    private final VideoFinder videoFinder;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final VideoCreator videoCreator;

    @Override
    public int collect(List<String> videoYoutubeIds) {
        videoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds.stream().distinct().toList());

        List<VideoResponse> videoResponses = fetchVideos(videoYoutubeIds);

        List<String> channelYoutubeIds = videoResponses.stream().map(VideoResponse::getChannelYoutubeId).toList();
        oneByOneChannelCollector.collect(channelYoutubeIds);

        return storeFromResponses(videoResponses);
    }

    private List<VideoResponse> fetchVideos(List<String> videoYoutubeIds) {
        return videoYoutubeIds.stream()
                .map(videoYoutubeId -> youtubeDataApiCaller.fetchVideos(List.of(videoYoutubeId)))
                .filter(videoListResponse -> !videoListResponse.getItems().isEmpty())
                .map(videoListResponse -> videoListResponse.getItems().get(0))
                .toList();
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
