package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.channel.domain.ChannelCreator;
import io.github.hamsteak.trendlapse.channel.domain.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.domain.ChannelCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(prefix = "collector", name = "channel-strategy", havingValue = "batch", matchIfMissing = true)
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchChannelCollector implements ChannelCollector {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final ChannelFinder channelFinder;
    private final ChannelCreator channelCreator;

    public int collect(List<String> channelYoutubeIds) {
        channelYoutubeIds = channelFinder.findMissingChannelYoutubeIds(channelYoutubeIds.stream().distinct().toList());

        log.info("Found {} missing channels.", channelYoutubeIds.size());

        List<ChannelResponse> channelResponses = fetchChannels(channelYoutubeIds);

        int storedCount = storeFromResponses(channelResponses);

        log.info("Stored {} channels.", storedCount);

        return storedCount;
    }

    private List<ChannelResponse> fetchChannels(List<String> channelYoutubeIds) {
        List<ChannelResponse> responses = new ArrayList<>();

        int startIndex = 0;
        while (startIndex < channelYoutubeIds.size()) {
            int endIndex = Math.min(startIndex + youtubeDataApiProperties.getMaxResultCount(), channelYoutubeIds.size());
            List<String> subFetchChannelYoutubeIds = channelYoutubeIds.subList(startIndex, endIndex);

            ChannelListResponse channelListResponse = youtubeDataApiCaller.fetchChannels(subFetchChannelYoutubeIds);
            responses.addAll(channelListResponse.getItems());

            startIndex += youtubeDataApiProperties.getMaxResultCount();
        }

        if (responses.size() != channelYoutubeIds.size()) {
            List<String> channelYoutubeIdsInResponses = responses.stream().map(ChannelResponse::getId).toList();
            List<String> diff = channelYoutubeIds.stream().filter(channelYoutubeId -> !channelYoutubeIdsInResponses.contains(channelYoutubeId)).toList();
            log.info("Expected {} channels, but only {} returned. Difference: {}", channelYoutubeIds.size(), channelYoutubeIdsInResponses.size(), diff);
        }

        return responses;
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
