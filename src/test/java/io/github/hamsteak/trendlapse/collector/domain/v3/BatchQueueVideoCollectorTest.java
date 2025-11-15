//package io.github.hamsteak.trendlapse.collector.domain.v3;
//
//import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
//import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
//import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.mockito.AdditionalAnswers;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.IntStream;
//import java.util.stream.Stream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class BatchQueueVideoCollectorTest {
//    @Mock
//    BatchVideoCollector batchVideoCollector;
//    @Mock
//    YoutubeDataApiProperties youtubeDataApiProperties;
//    @Mock
//    VideoFinder videoFinder;
//
//
//    @ParameterizedTest
//    @MethodSource("params")
//    void test(List<String> missingVideoYoutubeIds, List<String> existingVideoYoutubeIds) {
//        // given
//        List<String> allVideoYoutubeIds = Stream.of(missingVideoYoutubeIds, existingVideoYoutubeIds)
//                .flatMap(Collection::stream)
//                .distinct()
//                .toList();
//
//        // 테스트 시작 전 파라미터 값 유효성 검증.
//        assertThat(allVideoYoutubeIds.size()).isEqualTo(missingVideoYoutubeIds.size() + existingVideoYoutubeIds.size());
//
//        VideoUncollectedTrendingQueue videoUncollectedTrendingQueue = new VideoUncollectedTrendingQueue();
//        allVideoYoutubeIds.forEach(videoYoutubeId -> videoUncollectedTrendingQueue.add(new TrendingItem("RG1", 1, videoYoutubeId)));
//
//        VideoCollectedTrendingQueue videoCollectedTrendingQueue = new VideoCollectedTrendingQueue();
//
//        BatchQueueVideoCollector batchQueueVideoCollector = new BatchQueueVideoCollector(
//                videoUncollectedTrendingQueue,
//                videoCollectedTrendingQueue,
//                batchVideoCollector,
//                youtubeDataApiProperties,
//                videoFinder
//        );
//
//        when(youtubeDataApiProperties.getMaxResultCount())
//                .thenReturn(2);
//
//        when(videoFinder.findMissingVideoYoutubeIds(anyList()))
//                .thenAnswer(invocation -> invocation.<List<String>>getArgument(0).stream()
//                        .filter(missingVideoYoutubeIds::contains)
//                        .toList());
//
//        // when
//        batchQueueVideoCollector.collect();
//
//        // then
//        verify(batchVideoCollector).collect(argThat(list -> list.equals(missingVideoYoutubeIds)));
//    }
//
//    static Stream<Arguments> params() {
//        return Stream.of(
//                Arguments.of(List.of("missing-video-youtube-id"), List.of("existing-video-youtube-id")),
//                Arguments.of(List.of("missing-video-youtube-id"), List.of("existing-video-youtube-id-1", "existing-video-youtube-id-2"))
//        );
//    }
//
//    @Test
//    void 수집요청_크기가_미수집큐보다_큰_경우_비디오를_수집했을때_미수집큐만큼만_요청(
//            @Mock BatchVideoCollector batchVideoCollector,
//            @Mock YoutubeDataApiProperties youtubeDataApiProperties,
//            @Mock VideoFinder videoFinder
//    ) {
//        // given
//        List<String> missingVideoYoutubeIds = List.of("missing-video-youtube-id");
//
//        VideoUncollectedTrendingQueue videoUncollectedTrendingQueue = new VideoUncollectedTrendingQueue();
//        IntStream.range(0, missingVideoYoutubeIds.size())
//                .forEach(i -> videoUncollectedTrendingQueue.add(new TrendingItem("RG1", i + 1, missingVideoYoutubeIds.get(i))));
//
//        VideoCollectedTrendingQueue videoCollectedTrendingQueue = new VideoCollectedTrendingQueue();
//
//        BatchQueueVideoCollector batchQueueVideoCollector = new BatchQueueVideoCollector(
//                videoUncollectedTrendingQueue,
//                videoCollectedTrendingQueue,
//                batchVideoCollector,
//                youtubeDataApiProperties,
//                videoFinder
//        );
//
//        when(youtubeDataApiProperties.getMaxResultCount())
//                .thenReturn(2);
//
//        when(videoFinder.findMissingVideoYoutubeIds(anyList()))
//                .then(AdditionalAnswers.returnsFirstArg());
//
//        // when
//        batchQueueVideoCollector.collect();
//
//        // then
//        verify(batchVideoCollector).collect(argThat(list -> list.size() == missingVideoYoutubeIds.size()));
//    }
//
//    @Test
//    void 수집요청_크기가_미수집큐중_DB에_없는_아이템들_개수보다_큰_경우_비디오를_수집했을때_예외없음(
//            @Mock VideoCollectedTrendingQueue videoCollectedTrendingQueue,
//            @Mock BatchVideoCollector batchVideoCollector,
//            @Mock YoutubeDataApiProperties youtubeDataApiProperties,
//            @Mock VideoFinder videoFinder
//    ) {
//        // given
//        String existingVideoYoutubeId1 = "existing-video-youtube-id-1";
//        String existingVideoYoutubeId2 = "existing-video-youtube-id-2";
//        String missingVideoYoutubeId = "missing-video-youtube-id";
//        List<String> videoYoutubeIds = List.of(existingVideoYoutubeId1, existingVideoYoutubeId2, missingVideoYoutubeId);
//
//        VideoUncollectedTrendingQueue videoUncollectedTrendingQueue = new VideoUncollectedTrendingQueue();
//        IntStream.range(0, videoYoutubeIds.size())
//                .forEach(i -> videoUncollectedTrendingQueue.add(new TrendingItem("RG1", i + 1, videoYoutubeIds.get(i))));
//
//        when(youtubeDataApiProperties.getMaxResultCount())
//                .thenReturn(2);
//
//        when(videoFinder.findMissingVideoYoutubeIds(anyList()))
//                .thenReturn(List.of(missingVideoYoutubeId));
//
//        BatchQueueVideoCollector batchQueueVideoCollector = new BatchQueueVideoCollector(
//                videoUncollectedTrendingQueue,
//                videoCollectedTrendingQueue,
//                batchVideoCollector,
//                youtubeDataApiProperties,
//                videoFinder
//        );
//
//        // when
//        batchQueueVideoCollector.collect();
//
//        // then
//        verify(batchVideoCollector).collect(argThat(list -> list.size() == 1));
//    }
//}