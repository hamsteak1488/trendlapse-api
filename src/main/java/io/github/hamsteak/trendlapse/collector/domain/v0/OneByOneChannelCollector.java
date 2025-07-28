package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OneByOneChannelCollector {
    private final ChannelRepository channelRepository;
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    @Transactional
    public Channel collect(String youtubeId) {
        return channelRepository.findByYoutubeId(youtubeId)
                .orElseGet(() -> {
                    ChannelResponse channelResponse = youtubeDataApiCaller.fetchChannel(youtubeId);
                    return channelRepository.save(
                            Channel.builder()
                                    .youtubeId(youtubeId)
                                    .title(channelResponse.getSnippet().getTitle())
                                    .thumbnailUrl(channelResponse.getSnippet().getThumbnails().getHigh().getUrl())
                                    .build()
                    );
                });
    }
}
