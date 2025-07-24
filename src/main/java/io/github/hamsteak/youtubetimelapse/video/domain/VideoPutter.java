package io.github.hamsteak.youtubetimelapse.video.domain;

import io.github.hamsteak.youtubetimelapse.channel.domain.Channel;
import io.github.hamsteak.youtubetimelapse.channel.domain.ChannelPutter;
import io.github.hamsteak.youtubetimelapse.external.youtube.domain.YoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.youtubetimelapse.video.infrastructure.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VideoPutter {
    private final VideoRepository videoRepository;
    private final ChannelPutter channelPutter;
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    @Transactional
    public Video put(String youtubeId) {
        VideoResponse videoResponse = youtubeDataApiCaller.fetchVideo(youtubeId);
        Channel channel = channelPutter.put(videoResponse.getSnippet().getChannelId());

        return videoRepository.findByYoutubeId(youtubeId)
                .orElseGet(() -> videoRepository.save(
                                Video.builder()
                                        .youtubeId(youtubeId)
                                        .channel(channel)
                                        .title(videoResponse.getSnippet().getTitle())
                                        .thumbnailUrl(videoResponse.getSnippet().getThumbnails().getHigh().getUrl())
                                        .build()
                        )
                );
    }
}
