package io.github.hamsteak.trendlapse.video.application.component;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelReader;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoCreator {
    private final VideoRepository videoRepository;
    private final ChannelReader channelReader;

    @Transactional
    public Video create(String youtubeId, String channelYoutubeId, String title, String thumbnailUrl) {
        Channel channel = channelReader.readByYoutubeId(channelYoutubeId);

        if (thumbnailUrl == null) {
            log.warn("Video thumbnail url is null. (youtubeId={})", youtubeId);
        }

        return videoRepository.save(
                Video.builder()
                        .youtubeId(youtubeId)
                        .channel(channel)
                        .title(title)
                        .thumbnailUrl(thumbnailUrl)
                        .build()
        );
    }
}
