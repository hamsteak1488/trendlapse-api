package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelCreator;
import io.github.hamsteak.trendlapse.channel.domain.ChannelReader;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchChannelCollector {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final ChannelReader channelReader;
    private final ChannelCreator channelCreator;

    public void collect(List<String> channelYoutubeIds) {
        List<String> existingChannelYoutubeIds = channelReader.readByYoutubeIds(channelYoutubeIds)
                .stream()
                .map(Channel::getYoutubeId)
                .toList();

        List<String> fetchChannelYoutubeIds = channelYoutubeIds.stream()
                .filter(Predicate.not(existingChannelYoutubeIds::contains))
                .toList();

        // DB에 이미 Channel 데이터가 모두 존재하는 경우.
        if (fetchChannelYoutubeIds.isEmpty()) {
            return;
        }

        List<ChannelResponse> responses = new ArrayList<>();
        int fetchCount = (fetchChannelYoutubeIds.size() - 1) / youtubeDataApiProperties.getMaxFetchCount() + 1;
        for (int i = 0; i < fetchCount; i++) {
            int fromIndex = i * youtubeDataApiProperties.getMaxFetchCount();
            int toIndex = Math.min((i + 1) * youtubeDataApiProperties.getMaxFetchCount(), fetchChannelYoutubeIds.size());
            List<String> subFetchChannelYoutubeIds = fetchChannelYoutubeIds.subList(fromIndex, toIndex);

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
