package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelCreator;
import io.github.hamsteak.trendlapse.channel.application.dto.ChannelCreateDto;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelStorer {
    private final ChannelCreator channelCreator;

    public int store(List<ChannelItem> channelItems) {
        List<ChannelCreateDto> channelCreateDtos = channelItems.stream()
                .map(channelItem ->
                        new ChannelCreateDto(channelItem.getYoutubeId(), channelItem.getTitle(), channelItem.getThumbnailUrl()))
                .toList();
        return channelCreator.create(channelCreateDtos);
    }
}
