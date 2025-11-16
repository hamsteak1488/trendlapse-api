package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelCreator;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaChannelStorer implements ChannelStorer {
    private final ChannelCreator channelCreator;

    @Override
    public int store(List<ChannelItem> channelItems) {
        int storedCount = 0;

        for (ChannelItem channelItem : channelItems) {
            String channelYoutubeId = channelItem.getYoutubeId();

            channelCreator.create(channelYoutubeId, channelItem.getTitle(), channelItem.getThumbnailUrl());

            storedCount++;
        }

        return storedCount;
    }
}
