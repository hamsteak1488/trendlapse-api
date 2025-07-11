package io.github.hamsteak.youtubetimelapse.channel.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.YoutubeDataApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

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

        List<ChannelResponse> responses = youtubeDataApiCaller.fetchChannels(fetchChannelYoutubeIds).stream()
                .flatMap(response -> response.getItems().stream())
                .toList();

        responses.forEach(channelResponse ->
                channelCreator.create(
                        channelResponse.getId(),
                        channelResponse.getSnippet().getTitle(),
                        channelResponse.getSnippet().getThumbnails().getHigh().getUrl()
                )
        );
    }
}
