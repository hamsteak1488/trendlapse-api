package io.github.hamsteak.trendlapse.collector.application;

import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedChannel;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedRegion;
import io.github.hamsteak.trendlapse.collector.application.dto.FetchedVideo;
import io.github.hamsteak.trendlapse.collector.application.dto.RegionFetchedTrendingVideos;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.trendingsnapshot.domain.TrendingSnapshot;
import io.github.hamsteak.trendlapse.video.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
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
                .map(fetchedVideo ->
                        Video.builder()
                                .youtubeId(fetchedVideo.getYoutubeId())
                                .channelId(channelYoutubeIdEntityIdMap.get(fetchedVideo.getChannelYoutubeId()))
                                .title(fetchedVideo.getTitle())
                                .thumbnailUrl(fetchedVideo.getThumbnailUrl())
                                .build()
                ).toList();
    }

    public List<TrendingSnapshot> toTrendingSnapshots(
            List<RegionFetchedTrendingVideos> regionFetchedTrendingVideosList,
            Map<String, Long> videoYoutubeIdEntityIdMap,
            LocalDateTime captureTime
    ) {
        return regionFetchedTrendingVideosList.stream()
                .map(regionFetchedTrendingVideos -> {
                    List<Long> trendingSnapshotVideoIds = regionFetchedTrendingVideos.getFetchedTrendingVideos().stream()
                            .map(fetchedVideo -> videoYoutubeIdEntityIdMap.get(fetchedVideo.getYoutubeId()))
                            .toList();

                    return TrendingSnapshot.createTrendingSnapshot(
                            regionFetchedTrendingVideos.getRegionId(),
                            captureTime,
                            trendingSnapshotVideoIds
                    );
                })
                .toList();
    }
}
