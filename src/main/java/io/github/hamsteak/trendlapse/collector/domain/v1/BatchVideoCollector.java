package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.channel.domain.ChannelReader;
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
public class BatchVideoCollector {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final VideoFinder videoFinder;
    private final VideoCreator videoCreator;
    private final BatchChannelCollector batchChannelCollector;
    private final ChannelReader channelReader;

    public void collect(List<String> videoYoutubeIds) {
        List<String> missingVideoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds);

        // DB에 이미 Video 데이터가 모두 존재하는 경우.
        if (missingVideoYoutubeIds.isEmpty()) {
            return;
        }

        List<String> distinctMissingVideoYoutubeIds = missingVideoYoutubeIds.stream().distinct().toList();

        if (distinctMissingVideoYoutubeIds.size() != missingVideoYoutubeIds.size()) {
            log.warn("There are two or more identical YoutubeIds in the collection request list. {}", missingVideoYoutubeIds);
            missingVideoYoutubeIds = distinctMissingVideoYoutubeIds;
        }

        List<VideoResponse> responses = new ArrayList<>();
        int fetchCount = (missingVideoYoutubeIds.size() - 1) / youtubeDataApiProperties.getMaxFetchCount() + 1;
        for (int i = 0; i < fetchCount; i++) {
            int fromIndex = i * youtubeDataApiProperties.getMaxFetchCount();
            int toIndex = Math.min((i + 1) * youtubeDataApiProperties.getMaxFetchCount(), missingVideoYoutubeIds.size());
            List<String> subFetchVideoYoutubeIds = missingVideoYoutubeIds.subList(fromIndex, toIndex);

            VideoListResponse videoListResponse = youtubeDataApiCaller.fetchVideos(subFetchVideoYoutubeIds);
            responses.addAll(videoListResponse.getItems());
        }

        List<String> channelYoutubeIds = responses.stream()
                .map(VideoResponse::getSnippet)
                .map(VideoResponse.Snippet::getChannelId)
                .distinct()
                .toList();

        batchChannelCollector.collect(channelYoutubeIds);

        responses.forEach(videoResponse -> {
            long channelId = channelReader.readByYoutubeId(videoResponse.getSnippet().getChannelId()).getId();

            videoCreator.create(
                    videoResponse.getId(),
                    channelId,
                    videoResponse.getSnippet().getTitle(),
                    videoResponse.getSnippet().getThumbnails().getHigh().getUrl()
            );
        });
    }
}
