package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.collector.domain.v0.OneByOneTrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v0.OneByOneVideoCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.collector.domain.v2.BufferedBatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v3.FlexibleBufferedBatchTrendingCollector;
import io.github.hamsteak.trendlapse.collector.domain.v3.FlexibleTrendingBuffer;
import io.github.hamsteak.trendlapse.external.youtube.dto.*;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrendingCollectorTest {
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class Video {
        final String youtubeId;
        final String channelYoutubeId;
        final String title;
        final String thumbnailUrl;
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class Trending {
        final LocalDateTime dateTime;
        final String videoYoutubeId;
        final int rank;
        final String regionCode;
    }

    interface TrendingCollectorFactory {
        TrendingCollector create(Fixture fix);
    }

    @Builder
    static class Fixture {
        final VideoCollector videoCollector;
        final YoutubeDataApiProperties youtubeDataApiProperties;
        final YoutubeDataApiCaller youtubeDataApiCaller;
        final TrendingCreator trendingCreator;

        final FlexibleTrendingBuffer flexibleTrendingBuffer;
    }

    static Stream<Named<TrendingCollectorFactory>> implementations() {
        return Stream.of(
                Named.of("OneByOneTrendingCollector",
                        fix -> new OneByOneTrendingCollector(
                                mock(OneByOneVideoCollector.class),
                                fix.youtubeDataApiCaller,
                                fix.trendingCreator
                        )
                ),
                Named.of("BatchTrendingCollector",
                        fix -> new BatchTrendingCollector(
                                mock(BatchVideoCollector.class),
                                fix.youtubeDataApiProperties,
                                fix.youtubeDataApiCaller,
                                fix.trendingCreator
                        )
                ),
                Named.of("BufferedBatchTrendingCollector",
                        fix -> new BufferedBatchTrendingCollector(
                                mock(BatchVideoCollector.class),
                                fix.youtubeDataApiProperties,
                                fix.youtubeDataApiCaller,
                                fix.trendingCreator
                        )
                ),
                Named.of("FlexibleBufferedBatchTrendingCollector",
                        fix -> new FlexibleBufferedBatchTrendingCollector(
                                mock(BatchVideoCollector.class),
                                fix.youtubeDataApiProperties,
                                fix.youtubeDataApiCaller,
                                fix.trendingCreator,
                                fix.flexibleTrendingBuffer

                        )
                )
        );
    }

    @Builder
    @RequiredArgsConstructor
    private static class Case {
        final String name;
        final int collectSize;
        final int maxResultCount;
        final List<String> regionCodes;
        final List<Trending> expectedCreatedTrendings;
    }

    private static Stream<Case> cases() {
        return Stream.of(
                Case.builder()
                        .name("한 개의 데이터")
                        .collectSize(1)
                        .maxResultCount(1)
                        .regionCodes(List.of("RG1"))
                        .expectedCreatedTrendings(List.of(defaultTrending(1, "RG1")))
                        .build(),
                Case.builder()
                        .name("여러 데이터")
                        .collectSize(3)
                        .maxResultCount(3)
                        .regionCodes(List.of("RG1"))
                        .expectedCreatedTrendings(List.of(
                                defaultTrending(1, "RG1"),
                                defaultTrending(2, "RG1"),
                                defaultTrending(3, "RG1")
                        ))
                        .build(),
                Case.builder()
                        .name("여러 지역 & 여러 데이터")
                        .collectSize(3)
                        .maxResultCount(3)
                        .regionCodes(List.of("RG1", "RG2"))
                        .expectedCreatedTrendings(List.of(
                                defaultTrending(1, "RG1"),
                                defaultTrending(2, "RG1"),
                                defaultTrending(3, "RG1"),
                                defaultTrending(1, "RG2"),
                                defaultTrending(2, "RG2"),
                                defaultTrending(3, "RG2")
                        ))
                        .build(),
                Case.builder()
                        .name("여러 Fetch")
                        .collectSize(3)
                        .maxResultCount(1)
                        .regionCodes(List.of("RG1", "RG2"))
                        .expectedCreatedTrendings(List.of(
                                defaultTrending(1, "RG1"),
                                defaultTrending(2, "RG1"),
                                defaultTrending(3, "RG1"),
                                defaultTrending(1, "RG2"),
                                defaultTrending(2, "RG2"),
                                defaultTrending(3, "RG2")
                        ))
                        .build(),
                Case.builder()
                        .name("collectSize가 maxResult로 딱 나누어 떨어지지 않을 때")
                        .collectSize(5)
                        .maxResultCount(2)
                        .regionCodes(List.of("RG1", "RG2"))
                        .expectedCreatedTrendings(List.of(
                                defaultTrending(1, "RG1"),
                                defaultTrending(2, "RG1"),
                                defaultTrending(3, "RG1"),
                                defaultTrending(4, "RG1"),
                                defaultTrending(5, "RG1"),
                                defaultTrending(1, "RG2"),
                                defaultTrending(2, "RG2"),
                                defaultTrending(3, "RG2"),
                                defaultTrending(4, "RG2"),
                                defaultTrending(5, "RG2")
                        ))
                        .build(),
                Case.builder()
                        .name("collectSize가 maxResult보다 작을 때")
                        .collectSize(3)
                        .maxResultCount(10)
                        .regionCodes(List.of("RG1"))
                        .expectedCreatedTrendings(List.of(
                                defaultTrending(1, "RG1"),
                                defaultTrending(2, "RG1"),
                                defaultTrending(3, "RG1")
                        ))
                        .build()
        );
    }

    private static Stream<Arguments> params() {
        return implementations().flatMap(impl ->
                cases().map(tc ->
                        Arguments.arguments(impl.getName(), impl.getPayload(), tc.name, tc)
                )
        );
    }

    @ParameterizedTest(name = "({0}) x ({2})")
    @MethodSource("params")
    @DisplayName("모든 TrendingCollector 구현체에 대해 검증")
    void create_expected_items_from_api_items(String implName, TrendingCollectorFactory trendingCollectorFactory, String tcName, Case tc) {
        // given
        YoutubeDataApiProperties youtubeDataApiProperties =
                new YoutubeDataApiProperties("baseUrl", "apiKey", tc.maxResultCount, false, 3);
        YoutubeDataApiCaller youtubeDataApiCaller = new TrendingMockYoutubeDataApiCaller();
        TrendingCreator trendingCreator = mock(TrendingCreator.class);

        VideoFinder videoFinder = mock(VideoFinder.class);
        FlexibleTrendingBuffer flexibleTrendingBuffer = new FlexibleTrendingBuffer(videoFinder);

        lenient().when(videoFinder.existsByYoutubeId(anyString())).thenReturn(false);

        Fixture fix = Fixture.builder()
                .youtubeDataApiProperties(youtubeDataApiProperties)
                .youtubeDataApiCaller(youtubeDataApiCaller)
                .trendingCreator(trendingCreator)
                .flexibleTrendingBuffer(flexibleTrendingBuffer)
                .build();

        TrendingCollector trendingCollector = trendingCollectorFactory.create(fix);

        // when
        int collectedCount = trendingCollector.collect(defaultLocalDateTime(), tc.collectSize, tc.regionCodes);

        // then
        ArgumentCaptor<LocalDateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<String> videoYoutubeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> rankArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> regionCodeArgumentCaptor = ArgumentCaptor.forClass(String.class);

        // Create 횟수가 기대되는 Create Youtube ID 목록 길이와 일치하는지 검사.
        verify(trendingCreator, times(tc.expectedCreatedTrendings.size())).create(
                dateTimeArgumentCaptor.capture(), videoYoutubeIdArgumentCaptor.capture(), rankArgumentCaptor.capture(), regionCodeArgumentCaptor.capture());

        // Create할 때 인수로 넘겨진 Youtube Id를 모아놨을 때 기대되는 Create Youtube ID 목록과 구성이 일치하는지 검사.
        List<Trending> argTrendings = IntStream.range(0, tc.expectedCreatedTrendings.size())
                .mapToObj(i -> {
                    LocalDateTime argDateTime = dateTimeArgumentCaptor.getAllValues().get(i);
                    String argVideoYoutubeId = videoYoutubeIdArgumentCaptor.getAllValues().get(i);
                    int argRank = rankArgumentCaptor.getAllValues().get(i);
                    String argRegionCode = regionCodeArgumentCaptor.getAllValues().get(i);

                    return new Trending(argDateTime, argVideoYoutubeId, argRank, argRegionCode);
                }).toList();
        assertThat(argTrendings).containsExactlyInAnyOrderElementsOf(tc.expectedCreatedTrendings);

        assertThat(collectedCount).isEqualTo(tc.expectedCreatedTrendings.size());
    }

    private static class TrendingMockYoutubeDataApiCaller implements YoutubeDataApiCaller {
        private static final int TRENDING_LIST_LENGTH = 200;

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
                if (offset + i == TRENDING_LIST_LENGTH) {
                    break;
                }

                int rank = offset + i + 1;
                String videoYoutubeId = defaultVideoYoutubeId(regionCode, rank);
                items.add(videoResponse(
                        new Video(videoYoutubeId, defaultChannelYoutubeId(videoYoutubeId), defaultTitle(videoYoutubeId), defaultThumbnailUrl(videoYoutubeId)))
                );
            }

            String nextPageToken = offset + maxResultCount < TRENDING_LIST_LENGTH ? defaultPageToken(offset + maxResultCount) : null;

            return trendingListResponse(items, nextPageToken);
        }

        @Override
        public RegionListResponse fetchRegions() {
            return null;
        }
    }

    private static VideoResponse videoResponse(Video video) {
        VideoResponse.Snippet.Thumbnails.Thumbnail high = new VideoResponse.Snippet.Thumbnails.Thumbnail(video.thumbnailUrl);

        VideoResponse.Snippet.Thumbnails thumbs = new VideoResponse.Snippet.Thumbnails(high);

        VideoResponse.Snippet snippet = new VideoResponse.Snippet(video.title, video.channelYoutubeId, thumbs);

        VideoResponse resp = new VideoResponse(video.youtubeId, snippet);

        return resp;
    }

    private static TrendingListResponse trendingListResponse(List<VideoResponse> items, String pageToken) {
        return new TrendingListResponse(items, pageToken);
    }

    private static Trending makeTrending(LocalDateTime dateTime, String videoYoutubeId, int rank, String regionCode) {
        return new Trending(dateTime, videoYoutubeId, rank, regionCode);
    }

    private static Trending defaultTrending(int rank, String regionCode) {
        return new Trending(defaultLocalDateTime(), defaultVideoYoutubeId(regionCode, rank), rank, regionCode);
    }

    private static String defaultVideoYoutubeId(String regionCode, int rank) {
        return String.format("%s-videoYoutubeId-%d", regionCode, rank);
    }

    private static LocalDateTime defaultLocalDateTime() {
        return LocalDateTime.of(2025, 1, 1, 0, 0);
    }

    private static String defaultPageToken(int rank) {
        return String.format("offset-%d", rank);
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
}