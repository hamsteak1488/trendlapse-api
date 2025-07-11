package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.channel.domain.BatchChannelCollector;
import io.github.hamsteak.youtubetimelapse.channel.domain.ChannelReader;
import io.github.hamsteak.youtubetimelapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.youtubetimelapse.common.errors.exception.RestApiException;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.youtubetimelapse.video.domain.Video;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoCreator;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class BatchVideoCollector {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final VideoReader videoReader;
    private final VideoCreator videoCreator;
    private final BatchChannelCollector batchChannelCollector;
    private final ChannelReader channelReader;

    public void collect(List<String> videoYoutubeIds) {
        List<String> existingVideoYoutubeIds = videoReader.readByYoutubeIds(videoYoutubeIds)
                .stream()
                .map(Video::getYoutubeId)
                .toList();

        List<String> fetchVideoYoutubeIds = videoYoutubeIds.stream()
                .filter(Predicate.not(existingVideoYoutubeIds::contains))
                .toList();

        if (fetchVideoYoutubeIds.isEmpty()) {
            return;
        }

        List<VideoResponse> responses = youtubeDataApiCaller.fetchVideos(fetchVideoYoutubeIds).stream()
                .flatMap(response -> response.getItems().stream())
                .toList();

        List<String> channelYoutubeIds = responses.stream()
                .map(VideoResponse::getSnippet)
                .map(VideoResponse.Snippet::getChannelId)
                .distinct()
                .toList();

        batchChannelCollector.collect(channelYoutubeIds);

        responses.forEach(videoResponse -> {
            long channelId = channelReader.readByYoutubeId(videoResponse.getSnippet().getChannelId()).getId();

            if (videoResponse.getSnippet().getThumbnails().getStandard() == null) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to fetch thumbnail of video(id:" + videoResponse.getId() + ")");
            }

            videoCreator.create(
                    videoResponse.getId(),
                    channelId,
                    videoResponse.getSnippet().getTitle(),
                    videoResponse.getSnippet().getThumbnails().getStandard().getUrl()
            );
        });
    }
}
