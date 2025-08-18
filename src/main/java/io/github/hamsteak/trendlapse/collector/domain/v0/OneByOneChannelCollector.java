package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.channel.domain.ChannelCreator;
import io.github.hamsteak.trendlapse.channel.domain.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.domain.ChannelCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "channel-strategy", havingValue = "one-by-one")
@Component
@RequiredArgsConstructor
public class OneByOneChannelCollector implements ChannelCollector {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final ChannelFinder channelFinder;
    private final ChannelCreator channelCreator;

    @Override
    public int collect(List<String> channelYoutubeIds) {
        channelYoutubeIds = channelFinder.findMissingChannelYoutubeIds(channelYoutubeIds.stream().distinct().toList());

        List<ChannelResponse> channelResponses = fetchChannels(channelYoutubeIds);

        return storeFromResponses(channelResponses);
    }

    private List<ChannelResponse> fetchChannels(List<String> channelYoutubeIds) {
        return channelYoutubeIds.stream()
                .map(channelYoutubeId -> youtubeDataApiCaller.fetchChannels(List.of(channelYoutubeId)))
                .filter(channelListResponse -> !channelListResponse.getItems().isEmpty())
                .map(channelListResponse -> channelListResponse.getItems().get(0))
                .toList();
    }

    private int storeFromResponses(List<ChannelResponse> channelResponses) {
        int storedCount = 0;

        for (ChannelResponse channelResponse : channelResponses) {
            String channelYoutubeId = channelResponse.getId();

            channelCreator.create(channelYoutubeId, channelResponse.getSnippet().getTitle(), channelResponse.getSnippet().getThumbnails().getHigh().getUrl());

            storedCount++;
        }

        return storedCount;
    }
}
