package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OneByOneVideoCollector {
    private final VideoRepository videoRepository;
    private final OneByOneChannelCollector oneByOneChannelCollector;
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    @Transactional
    public Video collect(String youtubeId) {
        VideoResponse videoResponse = youtubeDataApiCaller.fetchVideo(youtubeId);
        Channel channel = oneByOneChannelCollector.collect(videoResponse.getSnippet().getChannelId());

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
