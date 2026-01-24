package io.github.hamsteak.trendlapse.collector.application;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedChannel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedRegion;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedVideo;
import io.github.hamsteak.trendlapse.collector.application.dto.RegionFetchedTrendingVideos;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.trending.video.domain.TrendingVideoRankingSnapshot;
import io.github.hamsteak.trendlapse.video.domain.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchedDataAssembler {
    public List<Region> toRegions(List<FetchedRegion> fetchedRegions) {
        return fetchedRegions.stream()
                .map(fetchedRegion -> new Region(fetchedRegion.getId(), fetchedRegion.getName()))
                .toList();
    }

    public List<Channel> toChannels(List<FetchedChannel> fetchedChannels) {
        return fetchedChannels.stream()
                .map(fetchedChannel ->
                        Channel.builder()
                                .youtubeId(fetchedChannel.getYoutubeId())
                                .title(fetchedChannel.getTitle())
                                .thumbnailUrl(fetchedChannel.getThumbnailUrl())
                                .build()
                )
                .toList();
    }

    public List<Video> toVideos(List<FetchedVideo> fetchedVideos, Map<String, Long> channelYoutubeIdEntityIdMap) {
        return fetchedVideos.stream()
                .filter(fetchedVideo -> isChannelMapped(fetchedVideo, channelYoutubeIdEntityIdMap))
                .map(fetchedVideo ->
                        Video.builder()
                                .youtubeId(fetchedVideo.getYoutubeId())
                                .channelId(channelYoutubeIdEntityIdMap.get(fetchedVideo.getChannelYoutubeId()))
                                .title(fetchedVideo.getTitle())
                                .thumbnailUrl(fetchedVideo.getThumbnailUrl())
                                .build()
                ).toList();
    }

    public List<TrendingVideoRankingSnapshot> toTrendingVideoRankingSnapshots(
            List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList,
            Map<String, Long> videoYoutubeIdEntityIdMap,
            LocalDateTime captureTime
    ) {
        return regionFetchedTrendingVideosList.stream()
                .map(regionFetchedTrendingVideos -> filterMappedVideos(regionFetchedTrendingVideos, videoYoutubeIdEntityIdMap))
                .map(regionFetchedTrendingVideos -> {
                    List<Long> itemIds = regionFetchedTrendingVideos.getFetchedTrendingVideos().stream()
                            .map(fetchedVideo -> videoYoutubeIdEntityIdMap.get(fetchedVideo.getYoutubeId()))
                            .toList();
                    List<Long> viewCounts = regionFetchedTrendingVideos.getFetchedTrendingVideos().stream()
                            .map(FetchedVideo::getViewCount)
                            .toList();
                    List<Long> likeCounts = regionFetchedTrendingVideos.getFetchedTrendingVideos().stream()
                            .map(FetchedVideo::getLikeCount)
                            .toList();
                    List<Long> commentCounts = regionFetchedTrendingVideos.getFetchedTrendingVideos().stream()
                            .map(FetchedVideo::getCommentCount)
                            .toList();

                    return TrendingVideoRankingSnapshot.createTrendingVideoRankingSnapshot(
                            regionFetchedTrendingVideos.getRegionId(),
                            captureTime,
                            itemIds,
                            viewCounts,
                            likeCounts,
                            commentCounts
                    );
                })
                .toList();
    }

    private boolean isChannelMapped(FetchedVideo fetchedVideo, Map<String, Long> channelYoutubeIdEntityIdMap) {
        if (!channelYoutubeIdEntityIdMap.containsKey(fetchedVideo.getChannelYoutubeId())) {
            log.info("Skipping video record creation: No matching channel found (videoYoutubeId={}, channelYoutubeId={}).",
                    fetchedVideo.getYoutubeId(), fetchedVideo.getChannelYoutubeId());
            return false;
        }
        return true;
    }

    private RegionFetchedTrendingVideos filterMappedVideos(
            RegionFetchedTrendingVideos regionFetchedTrendingVideos,
            Map<String, Long> videoYoutubeIdEntityIdMap
    ) {
        List<FetchedVideo> videoMissingFilteredTrendingVideos = regionFetchedTrendingVideos.getFetchedTrendingVideos().stream()
                .filter(fetchedTrendingVideo -> {
                    if (!videoYoutubeIdEntityIdMap.containsKey(fetchedTrendingVideo.getYoutubeId())) {
                        log.info("Skipping trending record creation: No matching video found (region={}, videoYoutubeId={}).",
                                regionFetchedTrendingVideos.getRegionId(), fetchedTrendingVideo.getYoutubeId());
                        return false;
                    }
                    return true;
                })
                .toList();
        return new RegionFetchedTrendingVideos(regionFetchedTrendingVideos.getRegionId(), videoMissingFilteredTrendingVideos);
    }
}
