package io.github.hamsteak.trendlapse.video.application.component;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.video.application.dto.VideoCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class JdbcBulkVideoCreator implements VideoCreator {
    private final ChannelFinder channelFinder;

    private final JdbcTemplate jdbcTemplate;
    private static final int INSERT_BATCH_SIZE = 1000;

    @Transactional
    @Override
    public int create(List<VideoCreateDto> videoCreateDtos) {
        Map<String, Channel> channelMap = getChannelMap(videoCreateDtos);

        List<VideoCreateDto> channelExistingVideoItems = videoCreateDtos.stream()
                .filter(videoItem -> channelMap.containsKey(videoItem.getChannelYoutubeId()))
                .toList();

        jdbcTemplate.batchUpdate(
                "INSERT INTO video(youtube_id, channel_id, title, thumbnail_url) VALUES (?, ?, ?, ?)",
                channelExistingVideoItems,
                INSERT_BATCH_SIZE,
                (ps, videoItem) -> {
                    Channel channel = channelMap.get(videoItem.getChannelYoutubeId());

                    ps.setString(1, videoItem.getYoutubeId());
                    ps.setLong(2, channel.getId());
                    ps.setString(3, videoItem.getTitle());
                    ps.setString(4, videoItem.getThumbnailUrl());
                }
        );

        return channelExistingVideoItems.size();
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
