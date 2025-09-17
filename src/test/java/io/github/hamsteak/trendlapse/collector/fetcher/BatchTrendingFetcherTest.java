package io.github.hamsteak.trendlapse.collector.fetcher;

import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;
import io.github.hamsteak.trendlapse.external.youtube.dto.*;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BatchTrendingFetcherTest {
    @Test
    @DisplayName("Trending 수집 확인")
    void test() {
        // given
        YoutubeDataApiProperties youtubeDataApiProperties =
                new YoutubeDataApiProperties("baseUrl", "apiKey", 3, false, 3);

        LocalDateTime dateTime = defaultLocalDateTime();
        List<TrendingItem> expectedTrendingItems = List.of(
                new TrendingItem(dateTime, "RG1", 1, defaultVideoYoutubeId("RG1", 1)),
                new TrendingItem(dateTime, "RG1", 2, defaultVideoYoutubeId("RG1", 2))
        );
        List<VideoResponse> videoResponses = expectedTrendingItems.stream().map(trendingItem -> {
            String videoYoutubeId = trendingItem.getVideoYoutubeId();
            return videoResponse(videoYoutubeId, defaultChannelYoutubeId(videoYoutubeId), defaultTitle(videoYoutubeId), defaultThumbnailUrl(videoYoutubeId));
        }).toList();

        YoutubeDataApiCaller youtubeDataApiCaller = new TrendingMockYoutubeDataApiCaller(videoResponses);

        BatchTrendingFetcher batchTrendingFetcher = new BatchTrendingFetcher(youtubeDataApiProperties, youtubeDataApiCaller);

        // when
        List<TrendingItem> trendingItems = batchTrendingFetcher.fetch(dateTime, 100, "RG1");

        // then
        assertThat(trendingItems).containsExactlyInAnyOrderElementsOf(expectedTrendingItems);
    }

    private static class TrendingMockYoutubeDataApiCaller implements YoutubeDataApiCaller {
        private final List<VideoResponse> videoResponses;

        public TrendingMockYoutubeDataApiCaller(List<VideoResponse> videoResponses) {
            this.videoResponses = videoResponses;
        }

        @Override
        public ChannelListResponse fetchChannels(List<String> channelYoutubeId) {
            return null;
        }

        @Override
        public VideoListResponse fetchVideos(List<String> videoYoutubeId) {
            return null;
        }

        @Override
        public TrendingListResponse fetchTrendings(int maxResultCount, String regionCode, String pageToken) {
            List<VideoResponse> items = new ArrayList<>();

            int offset = pageToken == null ? 0 : Integer.parseInt(pageToken.split("-")[1]);
            for (int i = 0; i < maxResultCount; i++) {
                if (offset + i == videoResponses.size()) {
                    break;
                }

                items.add(videoResponses.get(offset + i));
            }

            String nextPageToken = offset + maxResultCount < videoResponses.size() ? defaultPageToken(offset + maxResultCount) : null;

            return trendingListResponse(items, nextPageToken);
        }

        @Override
        public RegionListResponse fetchRegions() {
            return null;
        }
    }

    private static TrendingListResponse trendingListResponse(List<VideoResponse> items, String pageToken) {
        return new TrendingListResponse(items, pageToken);
    }

    private static String defaultVideoYoutubeId(String regionCode, int rank) {
        return String.format("%s-videoYoutubeId-%d", regionCode, rank);
    }

    private static String defaultPageToken(int rank) {
        return String.format("offset-%d", rank);
    }

    private static VideoResponse defaultVideoResponse(String regionCode, int rank) {
        String videoYoutubeId = defaultVideoYoutubeId(regionCode, rank);
        return videoResponse(videoYoutubeId, defaultChannelYoutubeId(videoYoutubeId), defaultTitle(videoYoutubeId), defaultThumbnailUrl(videoYoutubeId));
    }

    private static VideoResponse videoResponse(String youtubeId, String channelYoutubeId, String title, String thumbnailUrl) {
        VideoResponse.Snippet.Thumbnails.Thumbnail high = new VideoResponse.Snippet.Thumbnails.Thumbnail(thumbnailUrl);

        VideoResponse.Snippet.Thumbnails thumbs = new VideoResponse.Snippet.Thumbnails(high);

        VideoResponse.Snippet snippet = new VideoResponse.Snippet(title, channelYoutubeId, thumbs);

        VideoResponse resp = new VideoResponse(youtubeId, snippet);

        return resp;
    }

    private static String defaultChannelYoutubeId(String youtubeId) {
        return String.format("channelYoutubeId-%s", youtubeId);
    }

    private static String defaultTitle(String youtubeId) {
        return String.format("title-%s", youtubeId);
    }

    private static String defaultThumbnailUrl(String youtubeId) {
        return String.format("thumbnailUrl-%s", youtubeId);
    }

    private static LocalDateTime defaultLocalDateTime() {
        return LocalDateTime.of(2025, 1, 1, 0, 0);
    }
}