package io.github.hamsteak.trendlapse.channel.application.component;

import io.github.hamsteak.trendlapse.channel.application.dto.ChannelCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class JdbcBulkChannelCreator implements ChannelCreator {
    private final JdbcTemplate jdbcTemplate;
    private static final int INSERT_BATCH_SIZE = 1000;
    private static final String INSERT_SQL = "INSERT INTO channel(youtube_id, title, thumbnail_url) VALUES (?, ?, ?)";

    @Transactional
    @Override
    public int create(List<ChannelCreateDto> channelCreateDtos) {
        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                channelCreateDtos,
                INSERT_BATCH_SIZE,
                (ps, channelCreateDto) -> {
                    ps.setString(1, channelCreateDto.getYoutubeId());
                    ps.setString(2, channelCreateDto.getTitle());
                    ps.setString(3, channelCreateDto.getThumbnailUrl());
                }
        );

        return channelCreateDtos.size();
    }
}
