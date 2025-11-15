package io.github.hamsteak.trendlapse.channel.application.component;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChannelFinder {
    private final ChannelRepository channelRepository;

    @Transactional(readOnly = true)
    public List<Channel> findExistingChannels(List<String> channelYoutubeIds) {
        return channelRepository.findByYoutubeIdIn(channelYoutubeIds);
    }

    @Transactional(readOnly = true)
    public List<String> findMissingChannelYoutubeIds(List<String> channelYoutubeIds) {
        Map<String, Channel> existingChannelMap = new HashMap<>();
        findExistingChannels(channelYoutubeIds).forEach(
                channel -> existingChannelMap.put(channel.getYoutubeId(), channel));

        return channelYoutubeIds.stream()
                .filter(channelYoutubeId -> !existingChannelMap.containsKey(channelYoutubeId))
                .toList();
    }
}
