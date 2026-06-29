package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.application.YoutubeApiClient;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.*;
import io.micrometer.core.annotation.Timed;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Timed("youtube.api.call")
@Component
public class MockYoutubeApiClient implements YoutubeApiClient {
    private final Map<String, RawChannel> channelResponseMap = new ConcurrentHashMap<>();
    private final Map<String, RawVideo> videoResponseMap = new ConcurrentHashMap<>();
    private final Set<String> regionVisitSet = new HashSet<>();

    @Override
    public RawChannelListResponse fetchChannels(List<String> channelYoutubeIds) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new RawChannelListResponse(channelYoutubeIds.stream()
                .map(channelResponseMap::get)
                .toList());
    }

    @Override
    public RawVideoListResponse fetchVideos(List<String> videoYoutubeIds) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new RawVideoListResponse(
                videoYoutubeIds.stream()
                        .map(videoResponseMap::get)
                        .toList(),
                null
        );
    }

    @Override
    public RawVideoListResponse fetchTrendings(String regionId, String pageToken, int maxResultCount) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String nextPageToken = pageToken == null ? "page-token" : null;
        int offset = pageToken == null ? 0 : 50;

        putRegionMockDataIfAbsent(regionId);

        return new RawVideoListResponse(
                IntStream.range(1 + offset, 51 + offset)
                        .mapToObj(i -> videoResponseMap.get(regionId + "-video-youtube-id-" + i)).toList(),
                nextPageToken
        );
    }

    @Override
    public RawRegionListResponse fetchRegions() {
        // TODO: Mock Region Fetch 구현 필요
        return null;
    }

    private void putRegionMockDataIfAbsent(String regionId) {
        if (regionVisitSet.contains(regionId)) {
            return;
        }
        regionVisitSet.add(regionId);

        //
        IntStream.range(1, 101)
                .forEach(i -> {
                    String channelYoutubeId = regionId + "-channel-youtube-id-" + i;
                    channelResponseMap.put(channelYoutubeId, new RawChannel(channelYoutubeId,
                            new RawChannel.Snippet(regionId + "-channel-title-" + i,
                                    new RawChannel.Snippet.Thumbnails(
                                            new RawChannel.Snippet.Thumbnails.Thumbnail(regionId + "-channel-thumbnail-" + i)
                                    )
                            )
                    ));
                });

        IntStream.range(1, 101)
                .forEach(i -> {
                    String videoYoutubeId = regionId + "-video-youtube-id-" + i;
                    videoResponseMap.put(videoYoutubeId, new RawVideo(videoYoutubeId,
                            new RawVideo.Snippet(regionId + "-video-title-" + i, regionId + "-channel-youtube-id-" + i,
                                    new RawVideo.Snippet.Thumbnails(
                                            new RawVideo.Snippet.Thumbnails.Thumbnail(regionId + "-video-thumbnail-" + i)
                                    )
                            ),
                            new RawVideo.Statistics(100_000, 1000, 10)
                    ));
                });
    }
}
