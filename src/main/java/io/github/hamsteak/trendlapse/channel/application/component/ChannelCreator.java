package io.github.hamsteak.trendlapse.channel.application.component;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelCreator {
    private final ChannelRepository channelRepository;

    @Transactional
    public Channel create(String youtubeId, String title, String thumbnailUrl) {
        if (thumbnailUrl == null) {
            log.warn("Channel thumbnail url is null. (youtubeId={})", youtubeId);
        }

        return channelRepository.save(
                Channel.builder()
                        .youtubeId(youtubeId)
                        .title(title)
                        .thumbnailUrl(thumbnailUrl)
                        .build()
        );
    }
}
