package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.collector.application.component.collector.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.VideoCollector;
import io.github.hamsteak.trendlapse.collector.application.dto.VideoItem;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v0.OneByOneVideoCollector;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.VideoFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.VideoStorer;
import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
import lombok.Builder;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoCollectorTest {
    private interface VideoCollectorFactory {
        VideoCollector create(Fixture fix);
    }

    @Builder
    private static class Fixture {
        final ChannelCollector channelCollector;
        final VideoFinder videoFinder;
        final VideoFetcher videoFetcher;
        final VideoStorer videoStorer;
        final YoutubeDataApiProperties youtubeDataApiProperties;
    }

    private static Stream<Named<VideoCollectorFactory>> implementations() {
        return Stream.of(
                Named.of("OneByOneVideoCollector",
                        (fix) -> new OneByOneVideoCollector(
                                fix.channelCollector,
                                fix.videoFinder,
                                fix.videoFetcher,
                                fix.videoStorer
                        )
                ),
                Named.of("BatchVideoCollector",
                        (fix) -> new BatchVideoCollector(
                                fix.channelCollector,
                                fix.videoFinder,
                                fix.videoFetcher,
                                fix.videoStorer,
                                fix.youtubeDataApiProperties
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
        final List<VideoItem> expectedCreatedVideos;
    }

    private static Stream<Case> cases() {
        return Stream.of(
                Case.builder()
                        .name("한 개의 데이터")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(defaultVideoItem("A")))
                        .build(),
                Case.builder()
                        .name("여러 개의 데이터")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A", "B", "C"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(defaultVideoItem("A"), defaultVideoItem("B"), defaultVideoItem("C")))
                        .build(),
                Case.builder()
                        .name("DB에 없는 데이터만 DB에 저장하는지 검사")
                        .maxResultCount(50)
                        .requestVideoYoutubeIds(List.of("A", "B", "C"))
                        .existingVideoYoutubeIds(List.of("B"))
                        .expectedCreatedVideos(List.of(defaultVideoItem("A"), defaultVideoItem("C")))
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
                        .expectedCreatedVideos(List.of(defaultVideoItem("A"), defaultVideoItem("B")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestVideoYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(defaultVideoItem("A"), defaultVideoItem("B"), defaultVideoItem("C"), defaultVideoItem("D"), defaultVideoItem("E")))
                        .build(),
                Case.builder()
                        .name("MaxResultCount가 요청 데이터 목록 길이보다 작은 경우에도 정상 동작하는지 검사.")
                        .maxResultCount(2)
                        .requestVideoYoutubeIds(List.of("A", "B", "C", "D", "E"))
                        .existingVideoYoutubeIds(List.of())
                        .expectedCreatedVideos(List.of(defaultVideoItem("A"), defaultVideoItem("B"), defaultVideoItem("C"), defaultVideoItem("D"), defaultVideoItem("E")))
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
        ChannelCollector channelCollector = mock(ChannelCollector.class);
        VideoFinder videoFinder = mock(VideoFinder.class);
        VideoFetcher videoFetcher = new MockVideoFetcher();
        VideoStorer videoStorer = mock(VideoStorer.class);
        YoutubeDataApiProperties youtubeDataApiProperties = new YoutubeDataApiProperties("baseUrl", "apiKey", tc.maxResultCount, false, 3);

        when(videoFinder.findMissingVideoYoutubeIds(anyList()))
                .then(invocationOnMock -> {
                    List<String> videoYoutubeIds = invocationOnMock.getArgument(0);
                    return videoYoutubeIds.stream()
                            .filter(videoYoutubeId -> !tc.existingVideoYoutubeIds.contains(videoYoutubeId)).toList();
                });

        Fixture fix = Fixture.builder()
                .channelCollector(channelCollector)
                .videoFinder(videoFinder)
                .videoFetcher(videoFetcher)
                .videoStorer(videoStorer)
                .youtubeDataApiProperties(youtubeDataApiProperties)
                .build();

        VideoCollector videoCollector = videoCollectorFactory.create(fix);

        // when
        videoCollector.collect(tc.requestVideoYoutubeIds);

        // then

        ArgumentCaptor<List> storeVideoItemsArgCaptor = ArgumentCaptor.forClass(List.class);

        verify(videoStorer, atLeast(0)).store(storeVideoItemsArgCaptor.capture());

        List<VideoItem> argVideoItems = storeVideoItemsArgCaptor.getAllValues().stream()
                .flatMap(list -> ((List<VideoItem>) list).stream()).toList();

        assertThat(argVideoItems).containsExactlyInAnyOrderElementsOf(tc.expectedCreatedVideos);
    }

    private static class MockVideoFetcher implements VideoFetcher {
        @Override
        public List<VideoItem> fetch(List<String> videoYoutubeIds, int maxResultCount) {
            return videoYoutubeIds.stream()
                    .map(VideoCollectorTest::defaultVideoItem)
                    .toList();
        }
    }

    private static VideoItem defaultVideoItem(String youtubeId) {
        return new VideoItem(youtubeId, defaultChannelYoutubeId(youtubeId), defaultTitle(youtubeId), defaultThumbnailUrl(youtubeId));
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