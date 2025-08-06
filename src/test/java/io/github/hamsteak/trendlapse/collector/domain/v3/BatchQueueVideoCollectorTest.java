package io.github.hamsteak.trendlapse.collector.domain.v3;

import io.github.hamsteak.trendlapse.collector.domain.v1.BatchVideoCollector;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchQueueVideoCollectorTest {

    @Test
    void 수집요청_크기가_미수집큐보다_큰_경우_비디오를_수집했을때_미수집큐만큼만_요청(
            @Mock BatchVideoCollector batchVideoCollector,
            @Mock YoutubeDataApiProperties youtubeDataApiProperties,
            @Mock VideoFinder videoFinder
    ) {
        // given
        List<String> missingVideoYoutubeIds = List.of("missing-video-youtube-id");

        VideoUncollectedTrendingQueue videoUncollectedTrendingQueue = new VideoUncollectedTrendingQueue();
        IntStream.range(0, missingVideoYoutubeIds.size())
                .forEach(i -> videoUncollectedTrendingQueue.add(new TrendingItem(1, i + 1, missingVideoYoutubeIds.get(i))));

        VideoCollectedTrendingQueue videoCollectedTrendingQueue = new VideoCollectedTrendingQueue();

        BatchQueueVideoCollector batchQueueVideoCollector = new BatchQueueVideoCollector(
                videoUncollectedTrendingQueue,
                videoCollectedTrendingQueue,
                batchVideoCollector,
                youtubeDataApiProperties,
                videoFinder
        );

        when(youtubeDataApiProperties.getMaxResultCount())
                .thenReturn(2);

        when(videoFinder.findMissingVideoYoutubeIds(anyList()))
                .then(AdditionalAnswers.returnsFirstArg());

        // when
        batchQueueVideoCollector.collect();

        // then
        verify(batchVideoCollector).collect(argThat(list -> list.size() == missingVideoYoutubeIds.size()));
    }

    @Test
    void 수집요청_크기가_미수집큐중_DB에_없는_아이템들_개수보다_큰_경우_비디오를_수집했을때_예외없음(
            @Mock VideoCollectedTrendingQueue videoCollectedTrendingQueue,
            @Mock BatchVideoCollector batchVideoCollector,
            @Mock YoutubeDataApiProperties youtubeDataApiProperties,
            @Mock VideoFinder videoFinder
    ) {
        // given
        String existingVideoYoutubeId1 = "existing-video-youtube-id-1";
        String existingVideoYoutubeId2 = "existing-video-youtube-id-2";
        String missingVideoYoutubeId = "missing-video-youtube-id";
        List<String> videoYoutubeIds = List.of(existingVideoYoutubeId1, existingVideoYoutubeId2, missingVideoYoutubeId);

        VideoUncollectedTrendingQueue videoUncollectedTrendingQueue = new VideoUncollectedTrendingQueue();
        IntStream.range(0, videoYoutubeIds.size())
                .forEach(i -> videoUncollectedTrendingQueue.add(new TrendingItem(1, i + 1, videoYoutubeIds.get(i))));

        when(youtubeDataApiProperties.getMaxResultCount())
                .thenReturn(2);

        when(videoFinder.findMissingVideoYoutubeIds(anyList()))
                .thenReturn(List.of(missingVideoYoutubeId));

        BatchQueueVideoCollector batchQueueVideoCollector = new BatchQueueVideoCollector(
                videoUncollectedTrendingQueue,
                videoCollectedTrendingQueue,
                batchVideoCollector,
                youtubeDataApiProperties,
                videoFinder
        );

        // when
        batchQueueVideoCollector.collect();

        // then
        verify(batchVideoCollector).collect(argThat(list -> list.size() == 1));
    }
}