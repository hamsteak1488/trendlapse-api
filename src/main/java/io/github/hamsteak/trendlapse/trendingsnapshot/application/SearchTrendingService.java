package io.github.hamsteak.trendlapse.trendingsnapshot.application;

import io.github.hamsteak.trendlapse.channel.application.dto.ChannelView;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelRepository;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingSnapshotView;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingVideoView;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshot;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotRepository;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotVideo;
import io.github.hamsteak.trendlapse.video.application.dto.VideoView;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SearchTrendingService {
    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final TrendingSnapshotRepository trendingSnapshotRepository;
    private final TrendingSnapshotQueryRepository trendingSnapshotQueryRepository;

    private final CacheManager cacheManager;
    private final static String CACHE_NAME = "trendingVideoView";

    @Transactional(readOnly = true)
    @Timed("trending.search")
    public List<TrendingSnapshotView> search(TrendingSearchFilter filter) {
        List<TrendingSnapshot> trendingSnapshots = trendingSnapshotRepository.findByRegionIdAndCapturedAtBetween(
                filter.getRegionId(),
                filter.getStartDateTime(),
                filter.getEndDateTime()
        );

        loadPersistenceContextOfVideoAndChannel(trendingSnapshots);

        return trendingSnapshots.stream()
                .map(trendingSnapshot -> {
                    List<TrendingSnapshotVideo> trendingSnapshotVideos = trendingSnapshot.getTrendingSnapshotVideos();

                    List<TrendingVideoView> trendingVideoViews = IntStream.range(0, trendingSnapshotVideos.size())
                            .mapToObj(index -> createTrendingVideoView(trendingSnapshotVideos.get(index), index))
                            .toList();

                    return new TrendingSnapshotView(
                            trendingSnapshot.getId(),
                            trendingSnapshot.getRegionId(),
                            trendingSnapshot.getCapturedAt(),
                            trendingVideoViews
                    );
                })
                .toList();
    }

    private TrendingVideoView createTrendingVideoView(TrendingSnapshotVideo trendingSnapshotVideo, int index) {
        Video video = videoRepository.findById(trendingSnapshotVideo.getTrendingVideoId())
                .orElseThrow();
        Channel channel = channelRepository.findById(video.getChannelId())
                .orElseThrow();

        return new TrendingVideoView(
                trendingSnapshotVideo.getTrendingSnapshot().getId(),
                index,
                new VideoView(
                        video.getId(),
                        video.getChannelId(),
                        video.getYoutubeId(),
                        video.getTitle(),
                        video.getThumbnailUrl()
                ),
                new ChannelView(
                        channel.getId(),
                        channel.getYoutubeId(),
                        channel.getTitle(),
                        channel.getThumbnailUrl()
                )
        );
    }

    private void loadPersistenceContextOfVideoAndChannel(List<TrendingSnapshot> trendingSnapshots) {
        List<Long> videoIds = trendingSnapshots.stream()
                .map(TrendingSnapshot::getTrendingSnapshotVideos)
                .flatMap(Collection::stream)
                .map(TrendingSnapshotVideo::getTrendingVideoId)
                .toList();
        List<Video> videos = videoRepository.findAllById(videoIds);

        List<Long> channelIds = videos.stream()
                .map(Video::getChannelId)
                .toList();
        channelRepository.findAllById(channelIds);
    }

    @Transactional(readOnly = true)
    @Timed("trending.search")
    public List<TrendingSnapshotView> searchWithCache(TrendingSearchFilter filter) {
        String regionId = filter.getRegionId();

        List<LocalDateTime> captureTimesInDb = trendingSnapshotRepository.findCaptureTimesByCapturedAtBetween(
                filter.getRegionId(),
                filter.getStartDateTime(),
                filter.getEndDateTime()
        );

        List<TrendingSnapshotView> resultTrendingSnapshotViews = new ArrayList<>();

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new IllegalStateException("Cache not found.");
        }

        List<LocalDateTime> missingCaptureTimes = new ArrayList<>();

        for (LocalDateTime captureTime : captureTimesInDb) {
            String cacheKey = getCacheKey(regionId, captureTime);
            TrendingSnapshotView trendingSnapshotView = cache.get(cacheKey, TrendingSnapshotView.class);

            if (trendingSnapshotView == null) {
                missingCaptureTimes.add(captureTime);
            } else {
                resultTrendingSnapshotViews.add(trendingSnapshotView);
            }
        }

        if (!missingCaptureTimes.isEmpty()) {
            List<TrendingSnapshotView> queriedTrendingSnapshotViews =
                    trendingSnapshotQueryRepository.findViewByRegionAndCapturedAtIn(regionId, missingCaptureTimes);

            queriedTrendingSnapshotViews.forEach(trendingSnapshotView ->
                    cache.put(
                            getCacheKey(regionId, trendingSnapshotView.getCapturedAt()),
                            trendingSnapshotView
                    )
            );

            resultTrendingSnapshotViews.addAll(queriedTrendingSnapshotViews);
        }

        resultTrendingSnapshotViews.sort(Comparator.comparing(TrendingSnapshotView::getCapturedAt));

        return resultTrendingSnapshotViews;
    }

    private static String getCacheKey(String regionCode, LocalDateTime dateTime) {
        return regionCode + ":" + dateTime;
    }
}
