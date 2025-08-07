package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.channel.domain.ChannelCreator;
import io.github.hamsteak.trendlapse.channel.domain.ChannelFinder;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelResponse;
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
public class BatchChannelCollector {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final ChannelFinder channelFinder;
    private final ChannelCreator channelCreator;

    public void collect(List<String> channelYoutubeIds) {
        List<String> missingChannelYoutubeIds = channelFinder.findMissingChannelYoutubeIds(channelYoutubeIds);

        // DB에 이미 Channel 데이터가 모두 존재하는 경우.
        if (missingChannelYoutubeIds.isEmpty()) {
            return;
        }

        List<String> distinctMissingChannelYoutubeIds = missingChannelYoutubeIds.stream().distinct().toList();

        if (distinctMissingChannelYoutubeIds.size() != missingChannelYoutubeIds.size()) {
            log.warn("There are two or more identical YoutubeIds in the collection request list. {}", missingChannelYoutubeIds);
            missingChannelYoutubeIds = distinctMissingChannelYoutubeIds;
        }

        List<ChannelResponse> responses = new ArrayList<>();
        int fetchCount = (missingChannelYoutubeIds.size() - 1) / youtubeDataApiProperties.getMaxResultCount() + 1;
        for (int i = 0; i < fetchCount; i++) {
            int fromIndex = i * youtubeDataApiProperties.getMaxResultCount();
            int toIndex = Math.min((i + 1) * youtubeDataApiProperties.getMaxResultCount(), missingChannelYoutubeIds.size());
            List<String> subFetchChannelYoutubeIds = missingChannelYoutubeIds.subList(fromIndex, toIndex);

            ChannelListResponse channelListResponse = youtubeDataApiCaller.fetchChannels(subFetchChannelYoutubeIds);
            responses.addAll(channelListResponse.getItems());
        }

        responses.forEach(channelResponse ->
                channelCreator.create(
                        channelResponse.getId(),
                        channelResponse.getSnippet().getTitle(),
                        channelResponse.getSnippet().getThumbnails().getHigh().getUrl()
                )
        );
    }
}
