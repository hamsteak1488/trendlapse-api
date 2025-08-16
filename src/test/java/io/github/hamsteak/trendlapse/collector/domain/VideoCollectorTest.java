package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.collector.domain.v0.OneByOneChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.v0.OneByOneVideoCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.external.youtube.dto.*;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.VideoCreator;
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

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoCollectorTest {
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class Video {
        final String youtubeId;
        final String channelYoutubeId;
        final String title;
        final String thumbnailUrl;
    }

    private interface VideoCollectorFactory {
        VideoCollector create(Fixture fix);
    }

    @Builder
    private static class Fixture {
        final VideoFinder videoFinder;
        final YoutubeDataApiProperties youtubeDataApiProperties;
        final YoutubeDataApiCaller youtubeDataApiCaller;
        final VideoCreator videoCreator;
    }

    private static Stream<Named<VideoCollectorFactory>> implementations() {
        return Stream.of(
                Named.of("OneByOneVideoCollector",
                        (fix) -> new OneByOneVideoCollector(
                                mock(OneByOneChannelCollector.class),
                                fix.videoFinder,
                                fix.youtubeDataApiCaller,
                                fix.videoCreator
                        )
                ),
                Named.of("BatchVideoCollector",
                        (fix) -> new BatchVideoCollector(
                                mock(BatchChannelCollector.class),
                                fix.videoFinder,
                                fix.youtubeDataApiProperties,
                                fix.youtubeDataApiCaller,
                                fix.videoCreator
                        )
                )
        );
    }

    @Builder
    @RequiredArgsConstructor
    private static class Case {
        final String name;
        final int maxResultCount;
        final List<String> requestVideoYoutubeIds;
        final List<String> existingVideoYoutubeIds;
        final List<Video> expectedCreatedVideos;
    }

    private static Stream<Case> cases() {
        return Stream.of(
                Case.builder()
                        .name("한 개의 데이터")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(makeVideo("A")))
                        .build(),
                Case.builder()
                        .name("여러 개의 데이터")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A", "B", "C"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(makeVideo("A"), makeVideo("B"), makeVideo("C")))
                        .build(),
                Case.builder()
                        .name("DB에 없는 데이터만 DB에 저장하는지 검사")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A", "B", "C"))
                        .existingVideoYoutubeIds(List.of("B"))
                        .expectedCreatedVideos(List.of(makeVideo("A"), makeVideo("C")))
                        .build(),
                Case.builder()
                        .name("DB에 이미 모든 데이터가 존재")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A", "B"))
                        .existingVideoYoutubeIds(List.of("A", "B"))
                        .expectedCreatedVideos(List.of())
                        .build(),
                Case.builder()
                        .name("중복 제거 후 저장하는지 검사")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A", "A", "B", "B"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(makeVideo("A"), makeVideo("B")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestVideoYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(makeVideo("A"), makeVideo("B"), makeVideo("C"), makeVideo("D"), makeVideo("E")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestVideoYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(makeVideo("A"), makeVideo("B"), makeVideo("C"), makeVideo("D"), makeVideo("E")))
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
    @DisplayName("모든 VideoCollector 구현체에 대해 검증")
    void create_expected_items_from_api_items(String implName, VideoCollectorFactory videoCollectorFactory, String tcName, Case tc) {
        // given
        VideoFinder videoFinder = mock(VideoFinder.class);
        YoutubeDataApiProperties youtubeDataApiProperties = new YoutubeDataApiProperties("baseUrl", "apiKey", tc.maxResultCount, false, 3);
        YoutubeDataApiCaller youtubeDataApiCaller = new VideoMockYoutubeApiCaller();
        VideoCreator videoCreator = mock(VideoCreator.class);

        when(videoFinder.findMissingVideoYoutubeIds(anyList()))
                .then(invocationOnMock -> {
                    List<String> videoYoutubeIds = invocationOnMock.getArgument(0);
                    return videoYoutubeIds.stream()
                            .filter(videoYoutubeId -> !tc.existingVideoYoutubeIds.contains(videoYoutubeId)).toList();
                });

        Fixture fix = Fixture.builder()
                .videoFinder(videoFinder)
                .youtubeDataApiProperties(youtubeDataApiProperties)
                .youtubeDataApiCaller(youtubeDataApiCaller)
                .videoCreator(videoCreator)
                .build();

        VideoCollector videoCollector = videoCollectorFactory.create(fix);

        // when
        int collectedCount = videoCollector.collect(tc.requestVideoYoutubeIds);

        // then
        ArgumentCaptor<String> youtubeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> channelYoutubeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> thumbnailUrlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        // Create 횟수가 기대되는 Create Youtube ID 목록 길이와 일치하는지 검사.
        verify(videoCreator, times(tc.expectedCreatedVideos.size())).create(
                youtubeIdArgumentCaptor.capture(), channelYoutubeIdArgumentCaptor.capture(), titleArgumentCaptor.capture(), thumbnailUrlArgumentCaptor.capture());

        // Create할 때 인수로 넘겨진 Youtube Id를 모아놨을 때 기대되는 Create Youtube ID 목록과 구성이 일치하는지 검사.
        List<Video> argVideos = IntStream.range(0, tc.expectedCreatedVideos.size())
                .mapToObj(i -> {
                    String argYoutubeId = youtubeIdArgumentCaptor.getAllValues().get(i);
                    String argChannelYoutubeId = channelYoutubeIdArgumentCaptor.getAllValues().get(i);
                    String argTitle = titleArgumentCaptor.getAllValues().get(i);
                    String argThumbnailUrl = thumbnailUrlArgumentCaptor.getAllValues().get(i);

                    return new Video(argYoutubeId, argChannelYoutubeId, argTitle, argThumbnailUrl);
                }).toList();
        assertThat(argVideos).containsExactlyInAnyOrderElementsOf(tc.expectedCreatedVideos);

        assertThat(collectedCount).isEqualTo(tc.expectedCreatedVideos.size());
    }

    private static class VideoMockYoutubeApiCaller implements YoutubeDataApiCaller {
        @Override
        public ChannelListResponse fetchChannels(List<String> channelYoutubeId) {
            return null;
        }

        @Override
        public VideoListResponse fetchVideos(List<String> videoYoutubeIds) {
            return videoListResponse(videoYoutubeIds.stream()
                    .map(youtubeId -> videoResponse(makeVideo(youtubeId)))
                    .toList()
            );
        }

        @Override
        public TrendingListResponse fetchTrendings(int maxResultCount, String regionCode, String pageToken) {
            return null;
        }

        @Override
        public RegionListResponse fetchRegions() {
            return null;
        }
    }

    private static Video makeVideo(String youtubeId) {
        return new Video(youtubeId, defaultChannelYoutubeId(youtubeId), defaultTitle(youtubeId), defaultThumbnailUrl(youtubeId));
    }

    private static VideoResponse videoResponse(Video video) {
        VideoResponse.Snippet.Thumbnails.Thumbnail high = new VideoResponse.Snippet.Thumbnails.Thumbnail(video.thumbnailUrl);

        VideoResponse.Snippet.Thumbnails thumbs = new VideoResponse.Snippet.Thumbnails(high);

        VideoResponse.Snippet snippet = new VideoResponse.Snippet(video.title, video.channelYoutubeId, thumbs);

        VideoResponse resp = new VideoResponse(video.youtubeId, snippet);

        return resp;
    }

    private static VideoListResponse videoListResponse(List<VideoResponse> items) {
        return new VideoListResponse(items);
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