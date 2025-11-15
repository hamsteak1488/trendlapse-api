package io.github.hamsteak.trendlapse.purger.domain;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import io.github.hamsteak.trendlapse.purger.application.component.TrendingPurger;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class TrendingPurgerTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private TrendingRepository trendingRepository;

    @Test
    @DisplayName("기간이 만료된 데이터 삭제")
    void test() {
        // given
        Region region = regionRepository.save(Region.builder().regionCode("RG").name("Region").isoCode("RG").build());
        Channel channel = channelRepository.save(Channel.builder().youtubeId("channel-youtube-id").title("channel-title").thumbnailUrl("channel-thumbnail-url").build());
        Video video = videoRepository.save(Video.builder().youtubeId("video-youtube-id").channel(channel).title("video-title").thumbnailUrl("video-thumbnail-url").build());

        Duration expirationPeriod = Duration.ofDays(30);
        int batchSize = 1000;
        LocalDateTime baseDateTime = LocalDateTime.of(2025, 9, 1, 0, 0);
        Trending expired = trendingRepository.save(Trending.builder()
                .dateTime(baseDateTime.minus(expirationPeriod).minusMinutes(1))
                .region(region)
                .video(video)
                .rankValue(1)
                .build());
        Trending notExpired = trendingRepository.save(Trending.builder()
                .dateTime(baseDateTime.minus(expirationPeriod).plusMinutes(1))
                .region(region)
                .video(video)
                .rankValue(1)
                .build());

        entityManager.flush();
        entityManager.clear();

        TrendingPurger trendingPurger = new TrendingPurger(trendingRepository, expirationPeriod, batchSize);

        // when
        long purgedCount = trendingPurger.purge(baseDateTime);

        // then
        assertThat(purgedCount).isEqualTo(1);

        Optional<Trending> expiredFindResult = trendingRepository.findById(expired.getId());
        Optional<Trending> notExpiredFindResult = trendingRepository.findById(notExpired.getId());

        assertThat(expiredFindResult).isEmpty();
        assertThat(notExpiredFindResult).isNotEmpty();
        assertThat(notExpiredFindResult.get().getId()).isEqualTo(notExpired.getId());
    }
}