package io.github.hamsteak.trendlapse.channel.domain;

import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ChannelCreator {
    private final ChannelRepository channelRepository;

    @Transactional
    public Channel create(String youtubeId, String title, String thumbnailUrl) {
        return channelRepository.save(
                Channel.builder()
                        .youtubeId(youtubeId)
                        .title(title)
                        .thumbnailUrl(thumbnailUrl)
                        .build()
        );
    }
}
