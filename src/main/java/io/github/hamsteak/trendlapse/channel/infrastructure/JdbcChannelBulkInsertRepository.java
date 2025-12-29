package io.github.hamsteak.trendlapse.channel.infrastructure;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelBulkInsertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcChannelBulkInsertRepository implements ChannelBulkInsertRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final int INSERT_BATCH_SIZE = 1000;
    private static final String INSERT_SQL = "INSERT INTO channel(youtube_id, title, thumbnail_url) VALUES (?, ?, ?)";

    @Override
    public void bulkInsert(List<Channel> channels) {
        jdbcTemplate.batchUpdate(
                INSERT_SQL,
                channels,
                INSERT_BATCH_SIZE,
                (ps, channel) -> {
                    ps.setString(1, channel.getYoutubeId());
                    ps.setString(2, channel.getTitle());
                    ps.setString(3, channel.getThumbnailUrl());
                }
        );
    }
}
