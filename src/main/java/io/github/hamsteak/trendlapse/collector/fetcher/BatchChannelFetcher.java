package io.github.hamsteak.trendlapse.collector.fetcher;


import io.github.hamsteak.trendlapse.collector.domain.ChannelItem;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchChannelFetcher implements ChannelFetcher {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final YoutubeDataApiCaller youtubeDataApiCaller;

    public List<ChannelItem> fetch(List<String> channelYoutubeIds) {
        List<ChannelItem> channelItems = new ArrayList<>();

        int startIndex = 0;
        while (startIndex < channelYoutubeIds.size()) {
            int endIndex = Math.min(startIndex + youtubeDataApiProperties.getMaxResultCount(), channelYoutubeIds.size());
            List<String> subFetchChannelYoutubeIds = channelYoutubeIds.subList(startIndex, endIndex);

            ChannelListResponse channelListResponse = youtubeDataApiCaller.fetchChannels(subFetchChannelYoutubeIds);
            channelItems.addAll(channelListResponse.getItems().stream()
                    .map(channelResponse -> new ChannelItem(
                            channelResponse.getId(),
                            channelResponse.getSnippet().getTitle(),
                            channelResponse.getSnippet().getThumbnails().getHigh().getUrl()
                    )).toList());

            startIndex += youtubeDataApiProperties.getMaxResultCount();
        }

        if (channelItems.size() != channelYoutubeIds.size()) {
            List<String> channelYoutubeIdsFromItems = channelItems.stream().map(ChannelItem::getYoutubeId).toList();
            List<String> diff = channelYoutubeIds.stream().filter(channelYoutubeId -> !channelYoutubeIdsFromItems.contains(channelYoutubeId)).toList();
            log.info("Expected {} channels, but only {} returned. Difference: {}", channelYoutubeIds.size(), channelYoutubeIdsFromItems.size(), diff);
        }

        return channelItems;
    }
}
