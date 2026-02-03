package io.github.hamsteak.trendlapse.collector.application;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.channel.domain.ChannelBulkInsertRepository;
import io.github.hamsteak.trendlapse.channel.domain.ChannelRepository;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedChannel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedRegion;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedVideo;
import io.github.hamsteak.trendlapse.collector.application.dto.RegionFetchedTrendingVideos;
import io.github.hamsteak.trendlapse.collector.infrastructure.BlockingYoutubeApiFetcher;
import io.github.hamsteak.trendlapse.collector.infrastructure.NonblockingYoutubeApiFetcher;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.domain.RegionRepository;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshotBulkInsertRepository;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.domain.VideoBulkInsertRepository;
import io.github.hamsteak.trendlapse.video.domain.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectTrendingVideoRankingSnapshotService {
    private final BlockingYoutubeApiFetcher blockingYoutubeApiFetcher;
    private final NonblockingYoutubeApiFetcher nonblockingYoutubeApiFetcher;
    private final FetchedDataAssembler fetchedDataAssembler;

    private final RegionRepository regionRepository;
    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final ChannelBulkInsertRepository channelBulkInsertRepository;
    private final VideoBulkInsertRepository videoBulkInsertRepository;
    private final TrendingVideoRankingSnapshotBulkInsertRepository trendingVideoRankingSnapshotBulkInsertRepository;

    @Transactional
    public void collect(LocalDateTime captureTime) {
        List<String> regionIds = fetchAndStoreRegions();

        List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList = fetchRegionTrendingVideos(regionIds);

        fetchAndStoreChannels(regionFetchedTrendingVideosList);
        storeVideos(regionFetchedTrendingVideosList);
        storeTrendingVideoRankingSnapshots(regionFetchedTrendingVideosList, captureTime);
    }

    private List<String> fetchAndStoreRegions() {
        List<FetchedRegion> fetchedRegions = blockingYoutubeApiFetcher.fetchRegions();
        List<Region> regions = fetchedDataAssembler.toRegions(fetchedRegions);
        List<String> regionIds = regions.stream().map(Region::getId).toList();
        List<String> regionsIdsNotInDb = findRegionIdsNotInDb(regionIds);
        List<Region> regionsToInsert = regions.stream()
                .filter(region -> regionsIdsNotInDb.contains(region.getId()))
                .toList();
        regionRepository.saveAllAndFlush(regionsToInsert);
        return regionIds;
    }

    private void fetchAndStoreChannels(List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList) {
        Set<String> distinctChannelYoutubeIds = extractChannelYoutubeIds(regionFetchedTrendingVideosList);
        List<String> channelYoutubeIdsNotInDb = findChannelsNotInDbByYoutubeId(distinctChannelYoutubeIds.stream().toList());
        List<FetchedChannel> fetchedChannelsNotInDb = nonblockingYoutubeApiFetcher.fetchChannels(channelYoutubeIdsNotInDb);

        List<Channel> channelsNotInDb = fetchedDataAssembler.toChannels(fetchedChannelsNotInDb);
        channelBulkInsertRepository.bulkInsert(channelsNotInDb);
    }

    private void storeVideos(List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList) {
        Set<String> distinctChannelYoutubeIds = extractChannelYoutubeIds(regionFetchedTrendingVideosList);
        Set<String> distinctVideoYoutubeIds = extractVideoYoutubeIds(regionFetchedTrendingVideosList);
        List<String> videoYoutubeIdsNotInDb = findVideosNotInDbByYoutubeId(distinctVideoYoutubeIds.stream().toList());

        Map<String, FetchedVideo> fetchedVideoMap = regionFetchedTrendingVideosList.stream()
                .flatMap(regionFetchedTrendingVideos ->
                        regionFetchedTrendingVideos.getFetchedTrendingVideos().stream())
                .collect(Collectors.toMap(
                        fetchedVideo -> fetchedVideo.getYoutubeId(),
                        fetchedVideo -> fetchedVideo,
                        (fv1, fv2) -> fv1
                ));

        List<FetchedVideo> fetchedVideosNotInDb = videoYoutubeIdsNotInDb.stream()
                .map(fetchedVideoMap::get)
                .toList();

        Map<String, Long> channelYoutubeIdEntityIdMap =
                channelRepository.findByYoutubeIdIn(distinctChannelYoutubeIds.stream().toList()).stream()
                        .collect(Collectors.toMap(Channel::getYoutubeId, Channel::getId));
        List<Video> videosNotInDb = fetchedDataAssembler.toVideos(fetchedVideosNotInDb, channelYoutubeIdEntityIdMap);
        videoBulkInsertRepository.bulkInsert(videosNotInDb);
    }

    private List<RegionFetchedTrendingVideos> fetchRegionTrendingVideos(List<String> regionIds) {
        return nonblockingYoutubeApiFetcher.fetchTrendingVideos(regionIds).entrySet().stream()
                .map(entry ->
                        new RegionFetchedTrendingVideos(
                                entry.getKey(),
                                entry.getValue()
                        )
                ).toList();
    }

    private void storeTrendingVideoRankingSnapshots(
            List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList,
            LocalDateTime captureTime
    ) {
        Set<String> distinctVideoYoutubeIds = extractVideoYoutubeIds(regionFetchedTrendingVideosList);

        Map<String, Long> videoYoutubeIdEntityIdMap =
                videoRepository.findByYoutubeIdIn(distinctVideoYoutubeIds.stream().toList()).stream()
                        .collect(Collectors.toMap(Video::getYoutubeId, Video::getId));
        List<TrendingVideoRankingSnapshot> trendingVideoRankingSnapshots =
                fetchedDataAssembler.toTrendingVideoRankingSnapshots(
                        regionFetchedTrendingVideosList,
                        videoYoutubeIdEntityIdMap,
                        captureTime
                );
        trendingVideoRankingSnapshotBulkInsertRepository.bulkInsert(trendingVideoRankingSnapshots);
    }

    private List<String> findRegionIdsNotInDb(List<String> regionIds) {
        Set<String> regionIdsInDb = regionRepository.findAllById(regionIds).stream()
                .map(Region::getId)
                .collect(Collectors.toSet());

        return regionIds.stream()
                .filter(channelYoutubeId -> !regionIdsInDb.contains(channelYoutubeId))
                .toList();
    }

    private List<String> findChannelsNotInDbByYoutubeId(List<String> channelYoutubeIds) {
        Map<String, Channel> channelInDbMap = channelRepository.findByYoutubeIdIn(channelYoutubeIds).stream()
                .collect(Collectors.toMap(
                        channel -> channel.getYoutubeId(),
                        channel -> channel
                ));
        return channelYoutubeIds.stream()
                .filter(channelYoutubeId -> !channelInDbMap.containsKey(channelYoutubeId))
                .toList();
    }

    private List<String> findVideosNotInDbByYoutubeId(List<String> videoYoutubeIds) {
        Map<String, Video> videoInDbMap = videoRepository.findByYoutubeIdIn(videoYoutubeIds).stream()
                .collect(Collectors.toMap(
                        video -> video.getYoutubeId(),
                        video -> video
                ));
        return videoYoutubeIds.stream()
                .filter(videoYoutubeId -> !videoInDbMap.containsKey(videoYoutubeId))
                .toList();
    }

    private Set<String> extractChannelYoutubeIds(List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList) {
        return regionFetchedTrendingVideosList.stream()
                .map(RegionFetchedTrendingVideos::getFetchedTrendingVideos)
                .flatMap(Collection::stream)
                .map(FetchedVideo::getChannelYoutubeId)
                .collect(Collectors.toSet());
    }

    private Set<String> extractVideoYoutubeIds(List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList) {
        return regionFetchedTrendingVideosList.stream()
                .map(RegionFetchedTrendingVideos::getFetchedTrendingVideos)
                .flatMap(Collection::stream)
                .map(FetchedVideo::getYoutubeId)
                .collect(Collectors.toSet());
    }
}
