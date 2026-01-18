package io.github.hamsteak.trendlapse.trendingsnapshot.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.hamsteak.trendlapse.channel.application.dto.QChannelView;
import io.github.hamsteak.trendlapse.channel.domain.QChannel;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.TrendingSnapshotQueryRepository;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.QTrendingVideoView;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingSnapshotView;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingVideoView;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.QTrendingSnapshot;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.QTrendingSnapshotVideo;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshot;
import io.github.hamsteak.trendlapse.video.application.dto.QVideoView;
import io.github.hamsteak.trendlapse.video.domain.QVideo;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TrendingSnapshotQueryRepositoryImpl implements TrendingSnapshotQueryRepository {
    private final JPAQueryFactory query;

    public TrendingSnapshotQueryRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<TrendingSnapshotView> findViewByRegionAndCapturedAtIn(String regionId, List<LocalDateTime> captureTimes) {
        QTrendingSnapshotVideo trendingSnapshotVideo = QTrendingSnapshotVideo.trendingSnapshotVideo;
        QTrendingSnapshot trendingSnapshot = QTrendingSnapshot.trendingSnapshot;
        QVideo video = QVideo.video;
        QChannel channel = QChannel.channel;

        List<TrendingSnapshot> trendingSnapshots = query
                .selectFrom(trendingSnapshot)
                .where(trendingSnapshot.regionId.eq(regionId)
                        .and(trendingSnapshot.capturedAt.in(captureTimes)))
                .fetch();

        List<Long> trendingSnapshotIds = trendingSnapshots.stream()
                .map(TrendingSnapshot::getId)
                .toList();

        List<TrendingVideoView> trendingVideoViews = query
                .select(new QTrendingVideoView(
                        trendingSnapshotVideo.trendingSnapshot.id,
                        trendingSnapshotVideo.listIndex,
                        new QVideoView(video.id, video.channelId, video.youtubeId, video.title, video.thumbnailUrl),
                        new QChannelView(channel.id, channel.youtubeId, channel.title, channel.thumbnailUrl)
                ))
                .from(trendingSnapshotVideo)
                .join(video).on(video.id.eq(trendingSnapshotVideo.trendingVideoId))
                .join(channel).on(channel.id.eq(video.channelId))
                .where(trendingSnapshotVideo.trendingSnapshot.id.in(trendingSnapshotIds))
                .fetch();

        Map<Long, List<TrendingVideoView>> trendingVideoViewsMap = trendingVideoViews.stream()
                .collect(Collectors.groupingBy(TrendingVideoView::getTrendingSnapshotId));

        return trendingSnapshots.stream()
                .map(ts ->
                        new TrendingSnapshotView(
                                ts.getId(),
                                ts.getRegionId(),
                                ts.getCapturedAt(),
                                trendingVideoViewsMap.getOrDefault(ts.getId(), new ArrayList<>())
                        )
                ).toList();
    }
}
