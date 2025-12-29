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
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshot;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshotBulkInsertRepository;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectTrendingSnapshotService {
    private final BlockingYoutubeApiFetcher blockingYoutubeApiFetcher;
    private final NonblockingYoutubeApiFetcher nonblockingYoutubeApiFetcher;
    private final FetchedDataAssembler fetchedDataAssembler;

    private final RegionRepository regionRepository;
    private final ChannelRepository channelRepository;
    private final VideoRepository videoRepository;
    private final ChannelBulkInsertRepository channelBulkInsertRepository;
    private final VideoBulkInsertRepository videoBulkInsertRepository;
    private final TrendingSnapshotBulkInsertRepository trendingSnapshotBulkInsertRepository;

    @Transactional
    public void collect(LocalDateTime captureTime) {
        List<Region> regions = fetchAndStoreRegions();

        List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList = fetchRegionTrendingVideos(regions);

        List<String> distinctChannelYoutubeIds = extractChannelYoutubeIds(regionFetchedTrendingVideosList);
        List<String> distinctVideoYoutubeIds = extractVideoYoutubeIds(regionFetchedTrendingVideosList);

        fetchAndStoreChannels(distinctChannelYoutubeIds);
        fetchAndStoreVideos(distinctVideoYoutubeIds, distinctChannelYoutubeIds);
        storeTrendingSnapshots(regionFetchedTrendingVideosList, distinctVideoYoutubeIds, captureTime);
    }

    private List<Region> fetchAndStoreRegions() {
        List<FetchedRegion> fetchedRegions = blockingYoutubeApiFetcher.fetchRegions();
        List<Region> regions = fetchedDataAssembler.toRegions(fetchedRegions);
        regionRepository.saveAllAndFlush(regions);
        return regions;
    }

    private void fetchAndStoreChannels(List<String> channelYoutubeIds) {
        List<String> channelYoutubeIdsNotInDb = findChannelsNotInDbByYoutubeId(channelYoutubeIds);
        List<FetchedChannel> fetchedChannelsNotInDb = nonblockingYoutubeApiFetcher.fetchChannels(channelYoutubeIdsNotInDb);

        List<Channel> channelsNotInDb = fetchedDataAssembler.toChannels(fetchedChannelsNotInDb);
        channelBulkInsertRepository.bulkInsert(channelsNotInDb);
    }

    private void fetchAndStoreVideos(List<String> videoYoutubeIds, List<String> channelYoutubeIdsForMapping) {
        List<String> videoYoutubeIdsNotInDb = findVideosNotInDbByYoutubeId(videoYoutubeIds);
        List<FetchedVideo> fetchedVideosNotInDb = nonblockingYoutubeApiFetcher.fetchVideos(videoYoutubeIdsNotInDb);

        Map<String, Long> channelYoutubeIdEntityIdMap = channelRepository.findByYoutubeIdIn(channelYoutubeIdsForMapping).stream()
                .collect(Collectors.toMap(Channel::getYoutubeId, Channel::getId));
        List<Video> videosNotInDb = fetchedDataAssembler.toVideos(fetchedVideosNotInDb, channelYoutubeIdEntityIdMap);
        videoBulkInsertRepository.bulkInsert(videosNotInDb);
    }

    private List<RegionFetchedTrendingVideos> fetchRegionTrendingVideos(List<Region> regions) {
        List<String> regionIds = regions.stream()
                .map(Region::getId)
                .toList();

        return nonblockingYoutubeApiFetcher.fetchTrendingVideos(regionIds).entrySet().stream()
                .map(entry ->
                        new RegionFetchedTrendingVideos(
                                entry.getKey(),
                                entry.getValue()
                        )
                ).toList();
    }

    private void storeTrendingSnapshots(
            List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList,
            List<String> videoYoutubeIds,
            LocalDateTime captureTime
    ) {
        Map<String, Long> videoYoutubeIdEntityIdMap =
                videoRepository.findByYoutubeIdIn(videoYoutubeIds).stream()
                        .collect(Collectors.toMap(Video::getYoutubeId, Video::getId));
        List<TrendingSnapshot> trendingSnapshots =
                fetchedDataAssembler.toTrendingSnapshots(
                        regionFetchedTrendingVideosList,
                        videoYoutubeIdEntityIdMap,
                        captureTime
                );
        trendingSnapshotBulkInsertRepository.bulkInsert(trendingSnapshots);
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

    private List<String> extractChannelYoutubeIds(List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList) {
        return regionFetchedTrendingVideosList.stream()
                .map(RegionFetchedTrendingVideos::getFetchedTrendingVideos)
                .flatMap(Collection::stream)
                .map(FetchedVideo::getChannelYoutubeId)
                .distinct()
                .toList();
    }

    private List<String> extractVideoYoutubeIds(List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList) {
        return regionFetchedTrendingVideosList.stream()
                .map(RegionFetchedTrendingVideos::getFetchedTrendingVideos)
                .flatMap(Collection::stream)
                .map(FetchedVideo::getYoutubeId)
                .distinct()
                .toList();
    }
}
