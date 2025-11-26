package io.github.hamsteak.trendlapse.support.fixture;

import io.github.hamsteak.trendlapse.channel.application.dto.ChannelDetail;
import io.github.hamsteak.trendlapse.channel.domain.Channel;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.collector.application.dto.VideoItem;
import io.github.hamsteak.trendlapse.region.application.dto.RegionDetail;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.trending.application.dto.DateTimeTrendingDetailList;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail;
import io.github.hamsteak.trendlapse.video.application.dto.VideoDetail;
import io.github.hamsteak.trendlapse.video.domain.Video;

import java.time.LocalDateTime;
import java.util.List;

public class DomainFixture {

    private static final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.of(2025, 1, 1, 0, 0);

    // Trending
    public static TrendingItem createTrendingItem(String videoYoutubeId, String regionCode, int rank) {
        return new TrendingItem(DEFAULT_DATE_TIME, regionCode, rank, videoYoutubeId);
    }

    public static TrendingItem createTrendingItem(String regionCode, int rank) {
        return createTrendingItem(createVideoYoutubeId(regionCode, rank), regionCode, rank);
    }

    public static TrendingDetail createTrendingDetail(String regionCode, int rank, String videoYoutubeId, String channelYoutubeId) {
        return TrendingDetail.builder()
                .dateTime(DEFAULT_DATE_TIME)
                .rank(rank)
                .videoDetail(createVideoDetail(videoYoutubeId, 1L)) // channelId is arbitrary for fixture
                .channelDetail(createChannelDetail(channelYoutubeId))
                .build();
    }

    public static DateTimeTrendingDetailList createDateTimeTrendingDetailList(String regionCode, int rank) {
        String videoYoutubeId = createVideoYoutubeId(regionCode, rank);
        String channelYoutubeId = "channel-" + videoYoutubeId;
        return DateTimeTrendingDetailList.builder()
                .dateTime(DEFAULT_DATE_TIME)
                .items(List.of(createTrendingDetail(regionCode, rank, videoYoutubeId, channelYoutubeId)))
                .build();
    }

    // Video
    public static Video createVideo(Channel channel, String youtubeId) {
        return Video.builder()
                .youtubeId(youtubeId)
                .channel(channel)
                .title(youtubeId + "-title")
                .thumbnailUrl(youtubeId + "-thumbnail-url")
                .build();
    }

    public static VideoItem createVideoItem(String youtubeId) {
        return new VideoItem(youtubeId, youtubeId + "-channel", youtubeId + "-title", youtubeId + "-thumbnail-url");
    }

    public static VideoDetail createVideoDetail(String youtubeId, Long channelId) {
        return VideoDetail.builder()
                .id(1L) // arbitrary ID for fixture
                .channelId(channelId)
                .youtubeId(youtubeId)
                .title(youtubeId + "-title")
                .thumbnailUrl(youtubeId + "-thumbnail-url")
                .build();
    }

    public static String createVideoYoutubeId(String regionCode, int rank) {
        return String.format("%s-videoYoutubeId-%d", regionCode, rank);
    }

    // Channel
    public static Channel createChannel(String youtubeId) {
        return Channel.builder()
                .youtubeId(youtubeId)
                .title(youtubeId + "-title")
                .thumbnailUrl(youtubeId + "-thumbnail-url")
                .build();
    }

    public static ChannelItem createChannelItem(String youtubeId) {
        return new ChannelItem(youtubeId, youtubeId + "-title", youtubeId + "-thumbnail-url");
    }

    public static ChannelDetail createChannelDetail(String youtubeId) {
        return ChannelDetail.builder()
                .id(1L) // arbitrary ID for fixture
                .youtubeId(youtubeId)
                .title(youtubeId + "-title")
                .thumbnailUrl(youtubeId + "-thumbnail-url")
                .build();
    }

    // Region
    public static Region createRegion(String regionCode) {
        return Region.builder()
                .regionCode(regionCode)
                .name(regionCode + "-name")
                .isoCode(regionCode)
                .build();
    }

    public static RegionDetail createRegionDetail(String regionCode) {
        return RegionDetail.builder()
                .regionCode(regionCode)
                .name(regionCode + "-name")
                .build();
    }

    // Common
    public static LocalDateTime getDefaultDateTime() {
        return DEFAULT_DATE_TIME;
    }
}

