package io.github.hamsteak.youtubetimelapse.video.domain;

import io.github.hamsteak.youtubetimelapse.channel.domain.BatchChannelCollector;
import io.github.hamsteak.youtubetimelapse.channel.domain.ChannelReader;
import io.github.hamsteak.youtubetimelapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.youtubetimelapse.common.errors.exception.RestApiException;
import io.github.hamsteak.youtubetimelapse.external.youtube.YoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static io.github.hamsteak.youtubetimelapse.config.Constants.MAX_FETCH_COUNT;

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

        List<VideoResponse> responses = new ArrayList<>();
        int fetchCount = (fetchVideoYoutubeIds.size() - 1) / MAX_FETCH_COUNT + 1;
        for (int i = 0; i < fetchCount; i++) {
            int fromIndex = i * MAX_FETCH_COUNT;
            int toIndex = Math.min((i + 1) * MAX_FETCH_COUNT, fetchVideoYoutubeIds.size());
            List<String> subFetchVideoYoutubeIds = fetchVideoYoutubeIds.subList(fromIndex, toIndex);

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

            if (videoResponse.getSnippet().getThumbnails().getHigh() == null) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to fetch thumbnail of video(id:" + videoResponse.getId() + ")");
            }

            videoCreator.create(
                    videoResponse.getId(),
                    channelId,
                    videoResponse.getSnippet().getTitle(),
                    videoResponse.getSnippet().getThumbnails().getHigh().getUrl()
            );
        });
    }
}
