package io.github.hamsteak.trendlapse.channel.application.component;

import io.github.hamsteak.trendlapse.channel.application.dto.ChannelCreateDto;

import java.util.List;

public interface ChannelCreator {
    int create(List<ChannelCreateDto> channelCreateDtos);
}
