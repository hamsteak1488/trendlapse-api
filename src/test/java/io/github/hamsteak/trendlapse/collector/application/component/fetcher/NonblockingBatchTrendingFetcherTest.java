package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.NonblockingYoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonblockingBatchTrendingFetcherTest {
    @Test
    @DisplayName("Nonblocking 방식 Trending Fetch 동작 확인")
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

        NonblockingYoutubeDataApiCaller apiCaller = new NonblockingTrendingMockYoutubeDataApiCaller(videoResponses);

        NonblockingBatchTrendingFetcher trendingFetcher = new NonblockingBatchTrendingFetcher(apiCaller);

        // when
        List<TrendingItem> trendingItems = trendingFetcher.fetch(dateTime, 2, List.of("RG1"), 2);

        // then
        assertThat(trendingItems).containsExactlyInAnyOrderElementsOf(expectedTrendingItems);
    }

    private static class NonblockingTrendingMockYoutubeDataApiCaller implements NonblockingYoutubeDataApiCaller {
        private final List<VideoResponse> videoResponses;

        public NonblockingTrendingMockYoutubeDataApiCaller(List<VideoResponse> videoResponses) {
            this.videoResponses = videoResponses;
        }

        @Override
        public Mono<ChannelListResponse> fetchChannels(List<String> channelYoutubeId) {
            return null;
        }

        @Override
        public Mono<VideoListResponse> fetchVideos(List<String> videoYoutubeId) {
            return null;
        }

        @Override
        public Mono<TrendingListResponse> fetchTrendings(int maxResultCount, String regionCode, String pageToken) {
            List<VideoResponse> items = new ArrayList<>();

            int offset = pageToken == null ? 0 : Integer.parseInt(pageToken.split("-")[1]);
            for (int i = 0; i < maxResultCount; i++) {
                if (offset + i == videoResponses.size()) {
                    break;
                }

                items.add(videoResponses.get(offset + i));
            }

            String nextPageToken = offset + maxResultCount < videoResponses.size() ? defaultPageToken(offset + maxResultCount) : null;

            TrendingListResponse trendingListResponse = trendingListResponse(items, nextPageToken);

            return Mono.just(trendingListResponse);
        }

        @Override
        public Mono<RegionListResponse> fetchRegions() {
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