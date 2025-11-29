package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.infrastructure.ChannelRepository;
import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.region.application.component.RegionReader;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import io.github.hamsteak.trendlapse.trending.application.component.JpaTrendingCreator;
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

import java.util.List;

import static io.github.hamsteak.trendlapse.support.fixture.DomainFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class TrendingStorerTest {
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
        VideoFinder videoFinder = new VideoFinder(videoRepository);
        VideoReader videoReader = new VideoReader(videoRepository);
        JpaTrendingCreator jpaTrendingCreator = new JpaTrendingCreator(trendingRepository, videoFinder, videoReader, regionReader);
        TrendingStorer trendingStorer = new TrendingStorer(jpaTrendingCreator, videoFinder);

        Region region = regionRepository.save(createRegion("RG1"));
        Channel channel = channelRepository.save(createChannel("channel-youtube-id"));
        List<Video> videos = List.of(
                videoRepository.save(createVideo(channel, "video-1-youtube-id")),
                videoRepository.save(createVideo(channel, "video-2-youtube-id"))
        );
        List<TrendingItem> trendingItems = videos.stream()
                .map(video -> createTrendingItem(video.getYoutubeId(), region.getRegionCode(), 1))
                .toList();

        // when
        int storedCount = trendingStorer.store(trendingItems);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(storedCount).isEqualTo(trendingItems.size());
        List<Trending> trendings = trendingRepository.findByDateTime(getDefaultDateTime());
        List<Long> expectedVideoIds = videos.stream().map(Video::getId).toList();

        assertThat(trendings)
                .hasSize(trendingItems.size())
                .extracting(trending -> trending.getVideo().getId())
                .containsExactlyInAnyOrderElementsOf(expectedVideoIds);
    }
}
