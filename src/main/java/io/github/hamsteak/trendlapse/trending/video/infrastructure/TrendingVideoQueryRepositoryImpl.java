package io.github.hamsteak.trendlapse.trending.video.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.hamsteak.trendlapse.channel.application.dto.QChannelView;
import io.github.hamsteak.trendlapse.channel.domain.QChannel;
import io.github.hamsteak.trendlapse.trending.video.application.TrendingVideoQueryRepository;
import io.github.hamsteak.trendlapse.trending.video.application.dto.*;
import io.github.hamsteak.trendlapse.trending.video.domain.QTrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.QTrendingVideoRankingSnapshotItem;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotItem;
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
public class TrendingVideoQueryRepositoryImpl implements TrendingVideoQueryRepository {
    private final JPAQueryFactory query;

    public TrendingVideoQueryRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<TrendingVideoRankingSnapshotView> findRankingSnapshotViewByRegionAndCapturedAtIn(String regionId, List<LocalDateTime> captureTimes) {
        QTrendingVideoRankingSnapshotItem snapshotItem = QTrendingVideoRankingSnapshotItem.trendingVideoRankingSnapshotItem;
        QTrendingVideoRankingSnapshot snapshot = QTrendingVideoRankingSnapshot.trendingVideoRankingSnapshot;
        QVideo video = QVideo.video;
        QChannel channel = QChannel.channel;

        List<TrendingVideoRankingSnapshot> snapshots = query
                .selectFrom(snapshot)
                .where(snapshot.regionId.eq(regionId)
                        .and(snapshot.capturedAt.in(captureTimes)))
                .fetch();

        List<Long> snapshotIds = snapshots.stream()
                .map(TrendingVideoRankingSnapshot::getId)
                .toList();

        List<TrendingVideoRankingSnapshotItemView> trendingVideoRankingSnapshotItemViews = query
                .select(new QTrendingVideoRankingSnapshotItemView(
                        snapshotItem.snapshot.id,
                        snapshotItem.listIndex,
                        snapshotItem.viewCount,
                        snapshotItem.likeCount,
                        snapshotItem.commentCount,
                        new QVideoView(video.id, video.channelId, video.youtubeId, video.title, video.thumbnailUrl),
                        new QChannelView(channel.id, channel.youtubeId, channel.title, channel.thumbnailUrl)
                ))
                .from(snapshotItem)
                .join(video).on(video.id.eq(snapshotItem.videoId))
                .join(channel).on(channel.id.eq(video.channelId))
                .where(snapshotItem.snapshot.id.in(snapshotIds))
                .fetch();

        Map<Long, List<TrendingVideoRankingSnapshotItemView>> trendingVideoViewsMap = trendingVideoRankingSnapshotItemViews.stream()
                .collect(Collectors.groupingBy(TrendingVideoRankingSnapshotItemView::getSnapshotId));

        return snapshots.stream()
                .map(ts ->
                        new TrendingVideoRankingSnapshotView(
                                ts.getId(),
                                ts.getRegionId(),
                                ts.getCapturedAt(),
                                trendingVideoViewsMap.getOrDefault(ts.getId(), new ArrayList<>())
                        )
                ).toList();
    }

    @Override
    public List<TrendingVideoStatisticsView> findStatisticsByVideoId(long videoId) {
        QTrendingVideoRankingSnapshotItem snapshotItem = QTrendingVideoRankingSnapshotItem.trendingVideoRankingSnapshotItem;

        return query
                .select(new QTrendingVideoStatisticsView(
                        snapshotItem.snapshot.id,
                        snapshotItem.snapshot.regionId,
                        snapshotItem.snapshot.capturedAt,
                        snapshotItem.listIndex,
                        snapshotItem.viewCount,
                        snapshotItem.likeCount,
                        snapshotItem.commentCount))
                .from(snapshotItem)
                .innerJoin(snapshotItem.snapshot)
                .where(snapshotItem.videoId.eq(videoId))
                .orderBy(snapshotItem.snapshot.capturedAt.asc())
                .fetch();
    }

    @Override
    public List<TrendingVideoRankingSnapshotItem> findRankingSnapshotItemByRegionIdAndVideoIdIn(String regionId, List<Long> videoIds) {
        QTrendingVideoRankingSnapshotItem snapshotItem = QTrendingVideoRankingSnapshotItem.trendingVideoRankingSnapshotItem;

        return query
                .selectFrom(snapshotItem)
                .innerJoin(snapshotItem.snapshot)
                .where(snapshotItem.snapshot.regionId.eq(regionId), snapshotItem.videoId.in(videoIds))
                .fetch();
    }
}
