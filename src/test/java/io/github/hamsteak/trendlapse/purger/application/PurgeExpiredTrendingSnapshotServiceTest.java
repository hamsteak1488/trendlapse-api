package io.github.hamsteak.trendlapse.purger.application;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelRepository;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.domain.RegionRepository;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshot;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotRepository;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class PurgeExpiredTrendingSnapshotServiceTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private TrendingSnapshotRepository trendingSnapshotRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("기간이 만료된 데이터 삭제")
    void testPurgingExpiredTrendingSnapshot() {
        // given
        Region region = regionRepository.save(new Region("RG", "Region"));
        Channel channel = channelRepository.save(
                Channel.builder()
                        .youtubeId("channel-youtube-id")
                        .title("channel-title")
                        .thumbnailUrl("channel-thumbnail-url")
                        .build()
        );
        Video video = videoRepository.save(
                Video.builder()
                        .youtubeId("video-youtube-id")
                        .channelId(channel.getId())
                        .title("video-title")
                        .thumbnailUrl("video-thumbnail-url")
                        .build()
        );

        Duration expirationPeriod = Duration.ofDays(30);
        int batchSize = 1000;
        LocalDateTime currentDateTime = LocalDateTime.of(2025, 9, 1, 0, 0);
        TrendingSnapshot expired = trendingSnapshotRepository.save(
                TrendingSnapshot.createTrendingSnapshot(region.getId(), currentDateTime.minus(expirationPeriod).minusMinutes(1), List.of(video.getId()))
        );
        TrendingSnapshot notExpired = trendingSnapshotRepository.save(
                TrendingSnapshot.createTrendingSnapshot(region.getId(), currentDateTime.minus(expirationPeriod).plusMinutes(1), List.of(video.getId()))
        );

        entityManager.flush();
        entityManager.clear();

        PurgeExpiredTrendingSnapshotService purgeService =
                new PurgeExpiredTrendingSnapshotService(
                        trendingSnapshotRepository,
                        expirationPeriod,
                        batchSize
                );

        // when
        int purgedCount = purgeService.purge(currentDateTime);

        // then
        assertThat(purgedCount).isEqualTo(1);

        Optional<TrendingSnapshot> expiredFindResult = trendingSnapshotRepository.findById(expired.getId());
        Optional<TrendingSnapshot> notExpiredFindResult = trendingSnapshotRepository.findById(notExpired.getId());

        assertThat(expiredFindResult).isEmpty();
        assertThat(notExpiredFindResult).isNotEmpty();
        assertThat(notExpiredFindResult.get().getId()).isEqualTo(notExpired.getId());
    }
}