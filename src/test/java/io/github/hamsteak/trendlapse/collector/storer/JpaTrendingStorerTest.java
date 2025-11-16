package io.github.hamsteak.trendlapse.collector.storer;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import io.github.hamsteak.trendlapse.collector.application.component.storer.JpaTrendingStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.region.application.component.RegionReader;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import io.github.hamsteak.trendlapse.trending.application.component.TrendingCreator;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
import io.github.hamsteak.trendlapse.video.application.component.VideoReader;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.infrastructure.VideoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class JpaTrendingStorerTest {
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
    @DisplayName("Trending 저장 확인")
    void test() {
        // given
        RegionReader regionReader = new RegionReader(regionRepository);
        VideoReader videoReader = new VideoReader(videoRepository);
        TrendingCreator trendingCreator = new TrendingCreator(trendingRepository, videoReader, regionReader);
        VideoFinder videoFinder = new VideoFinder(videoRepository);
        JpaTrendingStorer jpaTrendingStorer = new JpaTrendingStorer(trendingCreator, videoFinder);

        Region region = regionRepository.save(Region.builder().regionCode("RG1").name("Region").isoCode("RG1").build());
        Channel channel = channelRepository.save(Channel.builder().youtubeId("channel-youtube-id").title("channel-title").thumbnailUrl("channel-thumbnail-url").build());
        List<Video> videos = List.of(
                videoRepository.save(Video.builder().youtubeId("video-1-youtube-id").channel(channel).title("video-1-title").thumbnailUrl("video-1-thumbnail-url").build()),
                videoRepository.save(Video.builder().youtubeId("video-2-youtube-id").channel(channel).title("video-2-title").thumbnailUrl("video-2-thumbnail-url").build())
        );
        List<TrendingItem> trendingItems = videos.stream().map(video -> new TrendingItem(defaultLocalDateTime(), region.getRegionCode(), 1, video.getYoutubeId())).toList();

        // when
        int storedCount = jpaTrendingStorer.store(trendingItems);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(storedCount).isEqualTo(trendingItems.size());
        List<Trending> trendings = trendingRepository.findByDateTime(defaultLocalDateTime());

        assertThat(trendings.size()).isEqualTo(trendingItems.size());
        for (int i = 0; i < trendings.size(); i++) {
            // EntityManager를 clear했기 때문에 인스턴스 대신 id 비교.
            assertThat(trendings.get(i).getVideo().getId()).isEqualTo(videos.get(i).getId());
        }
    }

    private static LocalDateTime defaultLocalDateTime() {
        return LocalDateTime.of(2025, 1, 1, 0, 0);
    }
}