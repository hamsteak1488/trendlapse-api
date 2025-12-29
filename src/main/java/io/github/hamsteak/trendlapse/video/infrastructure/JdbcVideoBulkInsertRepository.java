package io.github.hamsteak.trendlapse.video.infrastructure;

import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoBulkInsertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcVideoBulkInsertRepository implements VideoBulkInsertRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final int INSERT_BATCH_SIZE = 1000;

    @Override
    public void bulkInsert(List<Video> videos) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO video(youtube_id, channel_id, title, thumbnail_url, last_updated_at) VALUES (?, ?, ?, ?, ?)",
                videos,
                INSERT_BATCH_SIZE,
                (ps, video) -> {
                    ps.setString(1, video.getYoutubeId());
                    ps.setLong(2, video.getChannelId());
                    ps.setString(3, video.getTitle());
                    ps.setString(4, video.getThumbnailUrl());
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now(Clock.systemUTC())));
                }
        );
    }
}
