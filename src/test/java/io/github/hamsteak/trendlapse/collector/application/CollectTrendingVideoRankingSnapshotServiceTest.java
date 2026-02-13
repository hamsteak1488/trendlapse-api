package io.github.hamsteak.trendlapse.collector.application;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelBulkInsertRepository;
import io.github.hamsteak.trendlapse.channel.domain.ChannelRepository;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedChannel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedRegion;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedVideo;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.domain.RegionRepository;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotBulkInsertRepository;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotItem;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotRepository;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoBulkInsertRepository;
import io.github.hamsteak.trendlapse.video.domain.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CollectTrendingVideoRankingSnapshotServiceTest implements YoutubeApiFetcher {
    @Autowired
    FetchedDataAssembler fetchedDataAssembler;
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    ChannelBulkInsertRepository channelBulkInsertRepository;
    @Autowired
    VideoBulkInsertRepository videoBulkInsertRepository;
    @Autowired
    TrendingVideoRankingSnapshotBulkInsertRepository trendingVideoRankingSnapshotBulkInsertRepository;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    TrendingVideoRankingSnapshotRepository trendingVideoRankingSnapshotRepository;

    CollectTrendingVideoRankingSnapshotService sut;

    private List<FetchedRegion> fetchedRegions;
    private Map<String, FetchedChannel> fetchedChannelMap;
    private Map<String, FetchedVideo> fetchedVideoMap;
    private Map<String, List<FetchedVideo>> fetchedTrendingVideosMap;

    @BeforeEach
    void setUp() {
        sut = new CollectTrendingVideoRankingSnapshotService(
                this,
                fetchedDataAssembler,
                regionRepository,
                channelRepository,
                videoRepository,
                channelBulkInsertRepository,
                videoBulkInsertRepository,
                trendingVideoRankingSnapshotBulkInsertRepository,
                applicationEventPublisher
        );

        fetchedRegions = List.of(
                new FetchedRegion("KR", "Korea"),
                new FetchedRegion("US", "Korea"),
                new FetchedRegion("CA", "Canada")
        );

        fetchedChannelMap = Map.of(
                channelYoutubeId("KR"), fetchedChannel("KR"),
                channelYoutubeId("US"), fetchedChannel("US"),
                channelYoutubeId("CA"), fetchedChannel("CA")
        );

        fetchedVideoMap = Map.of(
                videoYoutubeId("KR"), fetchedVideo("KR"),
                videoYoutubeId("US"), fetchedVideo("US"),
                videoYoutubeId("CA"), fetchedVideo("CA")
        );

        fetchedTrendingVideosMap = Map.of(
                "KR", List.of(fetchedVideo("KR")),
                "US", List.of(fetchedVideo("US")),
                "CA", List.of(fetchedVideo("CA"))
        );
    }

    @Transactional
    @ParameterizedTest
    @MethodSource("dateTimeArgumentProviders")
    void test_collect(LocalDateTime dateTime) {
        // when
        sut.collect(dateTime);

        // then
        List<Region> savedRegions = regionRepository.findAll();
        List<Channel> savedChannels = channelRepository.findAll();
        List<Video> savedVideos = videoRepository.findAll();
        List<TrendingVideoRankingSnapshot> savedSnapshots = trendingVideoRankingSnapshotRepository.findAll();
        List<TrendingVideoRankingSnapshotItem> savedSnapshotItems = savedSnapshots.stream()
                .flatMap(snapshot -> snapshot.getItems().stream())
                .toList();

        List<FetchedRegion> savedRegionsMappedToDto = savedRegions.stream()
                .map(r -> new FetchedRegion(r.getId(), r.getName()))
                .toList();
        List<FetchedChannel> savedChannelsMappedToDto = savedChannels.stream()
                .map(channel -> new FetchedChannel(channel.getYoutubeId(), channel.getTitle(), channel.getThumbnailUrl()))
                .toList();

        assertThat(savedRegionsMappedToDto).containsExactlyInAnyOrderElementsOf(fetchedRegions);
        assertThat(savedChannelsMappedToDto).containsExactlyInAnyOrderElementsOf(fetchedChannelMap.values());
        assertThat(savedVideos).hasSize(3);
        assertThat(savedSnapshots).hasSize(3);
        assertThat(savedSnapshotItems).hasSize(3);
    }

    static Stream<Arguments> dateTimeArgumentProviders() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)),
                Arguments.of(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusNanos(1))
        );
    }

    @Override
    public List<FetchedRegion> fetchRegions() {
        return fetchedRegions.stream()
                .map(region -> new FetchedRegion(region.getId(), region.getName()))
                .toList();
    }

    @Override
    public List<FetchedChannel> fetchChannels(List<String> channelYoutubeIds) {
        return channelYoutubeIds.stream()
                .filter(fetchedChannelMap::containsKey)
                .map(fetchedChannelMap::get)
                .toList();
    }

    @Override
    public List<FetchedVideo> fetchVideos(List<String> videoYoutubeIds) {
        return videoYoutubeIds.stream()
                .filter(fetchedVideoMap::containsKey)
                .map(fetchedVideoMap::get)
                .toList();
    }

    @Override
    public Map<String, List<FetchedVideo>> fetchTrendingVideos(List<String> regionIds) {
        return regionIds.stream()
                .collect(Collectors.toMap(
                        regionId -> regionId,
                        regionId -> fetchedTrendingVideosMap.get(regionId)
                ));
    }

    private String channelYoutubeId(String regionId) {
        return regionId + "-Channel-youtubeId";
    }

    private FetchedChannel fetchedChannel(String regionId) {
        return new FetchedChannel(
                channelYoutubeId(regionId),
                regionId + "-Video-title",
                regionId + "-Video-thumbnailUrl"
        );
    }

    private String videoYoutubeId(String regionId) {
        return regionId + "-Video-youtubeId";
    }

    private FetchedVideo fetchedVideo(String regionId) {
        return new FetchedVideo(
                videoYoutubeId(regionId),
                channelYoutubeId(regionId),
                regionId + "-Video-title",
                regionId + "-Video-thumbnailUrl",
                33L,
                22L,
                11L
        );
    }
}