package io.github.hamsteak.trendlapse.video.application.component;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.video.application.dto.VideoCreateDto;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaVideoCreator implements VideoCreator {
    private final VideoRepository videoRepository;
    private final ChannelFinder channelFinder;

    @Transactional
    @Override
    public int create(List<VideoCreateDto> videoCreateDtos) {
        Map<String, Channel> channelMap = getChannelMap(videoCreateDtos);

        for (VideoCreateDto dto : videoCreateDtos) {
            Channel channel = channelMap.get(dto.getChannelYoutubeId());

            if (dto.getThumbnailUrl() == null) {
                log.warn("Video thumbnail url is null. (youtubeId={})", dto.getYoutubeId());
            }

            videoRepository.save(
                    Video.builder()
                            .youtubeId(dto.getYoutubeId())
                            .channel(channel)
                            .title(dto.getTitle())
                            .thumbnailUrl(dto.getThumbnailUrl())
                            .build()
            );
        }

        return videoCreateDtos.size();
    }

    private Map<String, Channel> getChannelMap(List<VideoCreateDto> dtos) {
        Map<String, Channel> channelMap = new HashMap<>();
        List<String> channelYoutubeIds = dtos.stream()
                .map(VideoCreateDto::getChannelYoutubeId)
                .toList();
        channelFinder.findByYoutubeIds(channelYoutubeIds)
                .forEach(channel -> channelMap.put(channel.getYoutubeId(), channel));

        return channelMap;
    }
}
