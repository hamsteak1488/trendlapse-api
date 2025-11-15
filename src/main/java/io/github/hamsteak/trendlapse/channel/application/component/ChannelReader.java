package io.github.hamsteak.trendlapse.channel.application.component;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import io.github.hamsteak.trendlapse.global.errors.exception.ChannelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ChannelReader {
    private final ChannelRepository channelRepository;

    @Transactional(readOnly = true)
    public Channel read(long channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException("Cannot find channel (id:" + channelId + ")"));
    }

    @Transactional(readOnly = true)
    public Channel readByYoutubeId(String youtubeId) {
        return channelRepository.findByYoutubeId(youtubeId)
                .orElseThrow(() -> new ChannelNotFoundException("Cannot find channel (youtubeId:" + youtubeId + ")"));
    }
}
