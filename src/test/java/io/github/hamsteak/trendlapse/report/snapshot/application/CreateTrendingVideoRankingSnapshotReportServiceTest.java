package io.github.hamsteak.trendlapse.report.snapshot.application;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelRepository;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.domain.RegionRepository;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReport;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReportRepository;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotRepository;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class CreateTrendingVideoRankingSnapshotReportServiceTest {
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    TrendingVideoRankingSnapshotRepository snapshotRepository;
    @Autowired
    TrendingVideoRankingSnapshotReportRepository trendingVideoRankingSnapshotReportRepository;
    @MockitoBean
    AiSnapshotReporter aiSnapshotReporter;

    long snapshotId;

    @Autowired
    CreateTrendingVideoRankingSnapshotReportService createTrendingVideoRankingSnapshotReportService;

    @BeforeEach
    void setUp() {
        String regionId = "KR";
        regionRepository.save(new Region(regionId, "Korea"));

        Channel channel = channelRepository.save(channel(regionId));
        Video video = videoRepository.save(video(regionId, channel.getId()));

        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 1, 1, 0);
        LocalDateTime previousDateTime = dateTime.minusHours(1);

        TrendingVideoRankingSnapshot previousSnapshot = TrendingVideoRankingSnapshot.createTrendingVideoRankingSnapshot(
                regionId,
                dateTime,
                List.of(video.getId()),
                List.of(108L),
                List.of(20L),
                List.of(2L)
        );
        TrendingVideoRankingSnapshot snapshot = TrendingVideoRankingSnapshot.createTrendingVideoRankingSnapshot(
                regionId,
                previousDateTime,
                List.of(video.getId()),
                List.of(111L),
                List.of(22L),
                List.of(3L)
        );

        snapshotRepository.save(previousSnapshot);
        snapshotId = snapshotRepository.save(snapshot).getId();

        when(aiSnapshotReporter.report(anyString()))
                .thenReturn("Analyzed Data.");
    }

    @Test
    void create_saves_reports() {
        // when
        createTrendingVideoRankingSnapshotReportService.create(snapshotId);

        // then
        List<TrendingVideoRankingSnapshotReport> snapshotReports = trendingVideoRankingSnapshotReportRepository.findAll();

        assertThat(snapshotReports).hasSize(1);

        TrendingVideoRankingSnapshotReport snapshotReport = snapshotReports.get(0);
        assertThat(snapshotReport).isNotNull();
        assertThat(snapshotReport.getSnapshotId()).isEqualTo(snapshotId);
        assertThat(snapshotReport.getSummary()).isNotNull();
    }

    private String channelYoutubeId(String regionId) {
        return regionId + "-Channel-youtubeId";
    }

    private Channel channel(String regionId) {
        return Channel.builder()
                .youtubeId(channelYoutubeId(regionId))
                .title(regionId + "-Channel-title")
                .thumbnailUrl(regionId + "-Channel-thumbnailUrl")
                .build();
    }

    private String videoYoutubeId(String regionId) {
        return regionId + "-Video-youtubeId";
    }

    private Video video(String regionId, long channelId) {
        return Video.builder()
                .youtubeId(videoYoutubeId(regionId))
                .channelId(channelId)
                .title(regionId + "-Video-title")
                .thumbnailUrl(regionId + "-Video-thumbnailUrl")
                .build();

    }
}