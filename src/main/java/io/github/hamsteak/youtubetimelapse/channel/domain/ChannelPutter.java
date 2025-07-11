package io.github.hamsteak.youtubetimelapse.channel.domain;

import io.github.hamsteak.youtubetimelapse.channel.infrastructure.ChannelRepository;
import io.github.hamsteak.youtubetimelapse.external.youtube.YoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ChannelPutter {
    private final ChannelRepository channelRepository;
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    @Transactional
    public Channel put(String youtubeId) {
        ChannelResponse channelResponse = youtubeDataApiCaller.fetchChannel(youtubeId);

        return channelRepository.findByYoutubeId(youtubeId)
                .orElseGet(() -> channelRepository.save(
                        Channel.builder()
                                .youtubeId(youtubeId)
                                .title(channelResponse.getSnippet().getTitle())
                                .thumbnailUrl(channelResponse.getSnippet().getThumbnails().getHigh().getUrl())
                                .build()
                        )
                );
    }
}
