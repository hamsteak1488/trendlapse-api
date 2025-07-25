package io.github.hamsteak.trendlapse.video.domain;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelReader;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VideoCreator {
    private final VideoRepository videoRepository;
    private final ChannelReader channelReader;

    @Transactional
    public Video create(String youtubeId, long channelId, String title, String thumbnailUrl) {
        Channel channel = channelReader.read(channelId);

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
