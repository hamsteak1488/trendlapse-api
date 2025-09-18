package io.github.hamsteak.trendlapse.collector.storer;

import io.github.hamsteak.trendlapse.channel.domain.ChannelCreator;
import io.github.hamsteak.trendlapse.collector.domain.ChannelItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaChannelStorer implements ChannelStorer {
    private final ChannelCreator channelCreator;

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
