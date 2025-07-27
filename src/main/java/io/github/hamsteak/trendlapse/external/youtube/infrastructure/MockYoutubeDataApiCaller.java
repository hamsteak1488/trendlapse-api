package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import io.github.hamsteak.trendlapse.external.youtube.dto.*;
import io.micrometer.core.annotation.Timed;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Primary
@Timed("youtube.api.call")
@Component
public class MockYoutubeDataApiCaller implements YoutubeDataApiCaller {
    private final Map<String, ChannelResponse> channelResponseMap = new ConcurrentHashMap<>();
    private final Map<String, VideoResponse> videoResponseMap = new ConcurrentHashMap<>();
    private final Set<String> regionVisitSet = new HashSet<>();

    @Override
    public ChannelResponse fetchChannel(String channelYoutubeId) {
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return channelResponseMap.get(channelYoutubeId);
    }

    @Override
    public ChannelListResponse fetchChannels(List<String> channelYoutubeIds) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new ChannelListResponse(channelYoutubeIds.stream()
                .map(channelResponseMap::get)
                .toList());
    }

    @Override
    public VideoResponse fetchVideo(String videoYoutubeId) {
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return videoResponseMap.get(videoYoutubeId);
    }

    @Override
    public VideoListResponse fetchVideos(List<String> videoYoutubeIds) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new VideoListResponse(videoYoutubeIds.stream()
                .map(videoResponseMap::get)
                .toList());
    }

    @Override
    public TrendingListResponse fetchTrendings(int count, String regionCode, String pageToken) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String nextPageToken = pageToken == null ? "page-token" : null;
        int offset = pageToken == null ? 0 : 50;

        putRegionMockDataIfAbsent(regionCode);

        return new TrendingListResponse(
                IntStream.range(1 + offset, 51 + offset)
                        .mapToObj(i -> videoResponseMap.get(regionCode + "-video-youtube-id-" + i)).toList(),
                nextPageToken
        );
    }

    private void putRegionMockDataIfAbsent(String regionCode) {
        if (regionVisitSet.contains(regionCode)) {
            return;
        }
        regionVisitSet.add(regionCode);

        //
        IntStream.range(1, 101)
                .forEach(i -> {
                    String channelYoutubeId = regionCode + "-channel-youtube-id-" + i;
                    channelResponseMap.put(channelYoutubeId, new ChannelResponse(channelYoutubeId,
                            new ChannelResponse.Snippet(regionCode + "-channel-title-" + i,
                                    new ChannelResponse.Snippet.Thumbnails(
                                            new ChannelResponse.Snippet.Thumbnails.Thumbnail(regionCode + "-channel-thumbnail-" + i)
                                    )
                            )
                    ));
                });

        IntStream.range(1, 101)
                .forEach(i -> {
                    String videoYoutubeId = regionCode + "-video-youtube-id-" + i;
                    videoResponseMap.put(videoYoutubeId, new VideoResponse(videoYoutubeId,
                            new VideoResponse.Snippet(regionCode + "-video-title-" + i, regionCode + "-channel-youtube-id-" + i,
                                    new VideoResponse.Snippet.Thumbnails(
                                            new VideoResponse.Snippet.Thumbnails.Thumbnail(regionCode + "-video-thumbnail-" + i)
                                    )
                            )
                    ));
                });
    }
}
