package io.github.hamsteak.youtubetimelapse.channel.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.YoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static io.github.hamsteak.youtubetimelapse.config.Constants.MAX_FETCH_COUNT;

@Component
@RequiredArgsConstructor
public class BatchChannelCollector {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
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

        if (fetchChannelYoutubeIds.isEmpty()) {
            return;
        }

        List<ChannelResponse> responses = new ArrayList<>();
        int fetchCount = (fetchChannelYoutubeIds.size() - 1) / MAX_FETCH_COUNT + 1;
        for (int i = 0; i < fetchCount; i++) {
            int fromIndex = i * MAX_FETCH_COUNT;
            int toIndex = Math.min((i + 1) * MAX_FETCH_COUNT, fetchChannelYoutubeIds.size());
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
