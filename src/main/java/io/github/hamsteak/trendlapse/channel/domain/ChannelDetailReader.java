package io.github.hamsteak.trendlapse.channel.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ChannelDetailReader {
    private final ChannelReader channelReader;


    @Transactional(readOnly = true)
    public ChannelDetail read(long channelId) {
        Channel channel = channelReader.read(channelId);

        return ChannelDetail.builder()
                .id(channelId)
                .youtubeId(channel.getYoutubeId())
                .title(channel.getTitle())
                .thumbnailUrl(channel.getThumbnailUrl())
                .build();
    }
}
