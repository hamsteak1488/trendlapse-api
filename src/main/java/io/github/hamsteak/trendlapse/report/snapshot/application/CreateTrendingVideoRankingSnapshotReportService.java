package io.github.hamsteak.trendlapse.report.snapshot.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelRepository;
import io.github.hamsteak.trendlapse.report.snapshot.domain.SnapshotReportAlreadyExistsException;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReport;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReportRepository;
import io.github.hamsteak.trendlapse.trending.video.application.TrendingVideoQueryRepository;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotItem;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotRepository;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateTrendingVideoRankingSnapshotReportService {
    private final TrendingVideoRankingSnapshotRepository snapshotRepository;
    private final TrendingVideoQueryRepository trendingVideoQueryRepository;
    private final TrendingVideoRankingSnapshotReportRepository snapshotReportRepository;
    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;
    private final AiSnapshotReporter aiSnapshotReporter;
    private final ObjectMapper objectMapper;

    @Value("${reporter.region-ids}")
    private final List<String> reportRegionIds;

    @Transactional
    public void create(Long snapshotId) {
        // If report already exists, throw exception.
        if (snapshotReportRepository.existsById(snapshotId)) {
            throw new SnapshotReportAlreadyExistsException("Snapshot report already exists.");
        }

        // Create input string data.
        TrendingVideoRankingSnapshot snapshot = snapshotRepository.findById(snapshotId)
                .orElseThrow(() -> new RuntimeException("Cannot find snapshot. (snapshotId=" + snapshotId + ")"));

        String reportInput = createReportInput(snapshot);

        if (!reportRegionIds.contains(snapshot.getRegionId())) {
            return;
        }

        log.debug("[region ID: {}] report input = {}", snapshot.getRegionId(), reportInput);

        // Create a report and return it.
        String aiAnalyzeResult = aiSnapshotReporter.report(reportInput);
        TrendingVideoRankingSnapshotReport report = new TrendingVideoRankingSnapshotReport(snapshot, aiAnalyzeResult);
        snapshotReportRepository.save(report);
    }

    private String createReportInput(TrendingVideoRankingSnapshot snapshot) {
        List<Video> videos = getVideos(snapshot);
        List<Channel> channels = getChannels(videos);

        List<VideoData> videoData = createVideoData(videos);
        List<ChannelData> channelData = channels.stream()
                .map(channel -> new ChannelData(channel.getId(), channel.getTitle()))
                .toList();

        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append("Snapshot Info:\n");
            stringBuilder.append("region code=").append(objectMapper.writeValueAsString(snapshot.getRegionId()));
            stringBuilder.append("\n\n");
            stringBuilder.append("Video Info:\n");
            stringBuilder.append(objectMapper.writeValueAsString(videoData));
            stringBuilder.append("\n\n");
            stringBuilder.append("Channel Info:\n");
            stringBuilder.append(objectMapper.writeValueAsString(channelData));
            stringBuilder.append("\n\n");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        stringBuilder.append("Analyze the data above and report briefly.");

        return stringBuilder.toString();
    }

    private List<Video> getVideos(TrendingVideoRankingSnapshot snapshot) {
        return videoRepository.findAllById(
                snapshot.getItems().stream()
                        .map(TrendingVideoRankingSnapshotItem::getVideoId)
                        .distinct()
                        .toList()
        );
    }

    private List<Channel> getChannels(List<Video> videos) {
        return channelRepository.findAllById(
                videos.stream()
                        .map(Video::getChannelId)
                        .distinct()
                        .toList()
        );
    }

    private List<VideoData> createVideoData(List<Video> videos) {
        List<Long> videoIds = videos.stream()
                .map(Video::getId)
                .toList();
        Map<Long, Video> videoMap = videos.stream()
                .collect(Collectors.toMap(
                        video -> video.getId(),
                        video -> video
                ));

        List<TrendingVideoRankingSnapshotItem> snapshotItems = trendingVideoQueryRepository.findRankingSnapshotItemByVideoIdIn(videoIds);

        return snapshotItems.stream()
                .collect(
                        Collectors.groupingBy(item -> item.getVideoId())
                ).entrySet().stream()
                .map(entry -> {
                    long videoId = entry.getKey();
                    List<TrendingVideoRankingSnapshotItem> items = entry.getValue();

                    Video video = videoMap.get(videoId);

                    List<Integer> rankHistory = items.stream()
                            .sorted(Comparator.comparing(o -> o.getSnapshot().getCapturedAt()))
                            .map(item -> item.getListIndex() + 1)
                            .toList();

                    if (rankHistory.isEmpty()) {
                        throw new IllegalStateException("rankHistory is empty.");
                    }

                    return new VideoData(videoId, video.getChannelId(), video.getTitle(), rankHistory);
                })
                .sorted((v1, v2) -> compareLastRank(v1, v2))
                .toList();
    }

    private static int compareLastRank(VideoData v1, VideoData v2) {
        int v1LastRank = v1.getRankHistory().get(v1.getRankHistory().size() - 1);
        int v2LastRank = v2.getRankHistory().get(v2.getRankHistory().size() - 1);
        return Integer.compare(v1LastRank, v2LastRank);
    }

    @Getter
    @RequiredArgsConstructor
    public static class VideoData {
        private final long id;
        private final long channelId;
        private final String title;
        private final List<Integer> rankHistory;
    }

    @Getter
    @RequiredArgsConstructor
    public static class ChannelData {
        private final long id;
        private final String title;
    }
}
