package io.github.hamsteak.trendlapse.channel.application.component;

import io.github.hamsteak.trendlapse.channel.application.dto.ChannelCreateDto;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaChannelCreator implements ChannelCreator {
    private final ChannelRepository channelRepository;

    @Transactional
    @Override
    public int create(List<ChannelCreateDto> channelCreateDtos) {
        for (ChannelCreateDto dto : channelCreateDtos) {
            if (dto.getThumbnailUrl() == null) {
                log.warn("Channel thumbnail url is null. (youtubeId={})", dto.getYoutubeId());
            }

            channelRepository.save(
                    Channel.builder()
                            .youtubeId(dto.getYoutubeId())
                            .title(dto.getTitle())
                            .thumbnailUrl(dto.getThumbnailUrl())
                            .build()
            );
        }

        return channelCreateDtos.size();
    }
}
