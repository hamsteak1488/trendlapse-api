package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.collector.domain.ChannelCollector;
import io.github.hamsteak.trendlapse.collector.domain.VideoItem;
import io.github.hamsteak.trendlapse.collector.fetcher.VideoFetcher;
import io.github.hamsteak.trendlapse.collector.storer.VideoStorer;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchVideoCollectorTest {
    @Mock
    ChannelCollector channelCollector;
    @Mock
    VideoFinder videoFinder;
    @Mock
    VideoFetcher videoFetcher;
    @Mock
    VideoStorer videoStorer;
    @Mock
    YoutubeDataApiProperties youtubeDataApiProperties;
    @InjectMocks
    BatchVideoCollector sut;

    // 1) 신규만 조회
    @Test
    void collect_queries_only_non_existing_ids_and_saves_them() {
        // given
        int maxResultCount = 3;

        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);
        when(videoFinder.findMissingVideoYoutubeIds(List.of("A", "B", "C"))).thenReturn(List.of("A", "C"));

        VideoItem videoItemA = new VideoItem("A", "channelA", "titleA", "urlA");
        VideoItem videoItemC = new VideoItem("C", "channelC", "titleC", "urlC");
        when(videoFetcher.fetch(List.of("A", "C"), maxResultCount))
                .thenReturn(List.of(videoItemA, videoItemC));

        // when
        sut.collect(List.of("A", "B", "C"));

        // then
        verify(videoFetcher, times(1)).fetch(List.of("A", "C"), maxResultCount);
        verify(videoFetcher, never()).fetch(List.of("A", "B", "C"), maxResultCount);
        verify(channelCollector).collect(List.of("channelA", "channelC"));
        verify(videoStorer, times(1)).store(List.of(videoItemA, videoItemC));
    }

    // 2) 빈 응답 스킵
    @Test
    void collect_skips_when_items_empty() {
        // given
        int maxResultCount = 3;

        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);
        when(videoFinder.findMissingVideoYoutubeIds(anyList())).thenReturn(List.of("A", "B"));
        VideoItem videoItemA = new VideoItem("A", "channelA", "titleA", "urlA");
        VideoItem videoItemB = new VideoItem("B", "channelB", "titleB", "urlB");

        when(videoFetcher.fetch(List.of("A", "B"), maxResultCount))
                .thenReturn(List.of(videoItemB)); // empty

        // when
        sut.collect(List.of("A", "B"));

        // then
        verify(videoStorer, times(1)).store(List.of(videoItemB));
    }

    // 3) 매핑 정확성
    @Test
    void collect_maps_response_to_entity_fields() {
        // given
        int maxResultCount = 3;

        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);
        VideoItem videoItemZ = new VideoItem("Z", "channelZ", "titleZ", "urlZ");

        when(videoFinder.findMissingVideoYoutubeIds(List.of("Z"))).thenReturn(List.of("Z"));
        when(videoFetcher.fetch(List.of("Z"), maxResultCount))
                .thenReturn(List.of(videoItemZ));

        // when
        sut.collect(List.of("Z"));

        // then
        ArgumentCaptor<List> storeVideoItemsArgCaptor = ArgumentCaptor.forClass(List.class);
        verify(videoStorer).store(storeVideoItemsArgCaptor.capture());

        VideoItem videoItem = ((List<VideoItem>) storeVideoItemsArgCaptor.getValue()).get(0);
        assertThat(videoItem.getYoutubeId()).isEqualTo("Z");
        assertThat(videoItem.getChannelYoutubeId()).isEqualTo("channelZ");
        assertThat(videoItem.getTitle()).isEqualTo("titleZ");
        assertThat(videoItem.getThumbnailUrl()).isEqualTo("urlZ");
    }

    // 4) 중복 입력 동작 문서화 (현재 구현: 중복 호출 발생)
    @Test
    void collect_calls_api_per_each_input_even_if_duplicate() {
        int maxResultCount = 3;

        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);
        VideoItem videoItemD = new VideoItem("D", "channelD", "titleD", "urlD");

        when(videoFinder.findMissingVideoYoutubeIds(List.of("D"))).thenReturn(List.of("D"));
        when(videoFetcher.fetch(List.of("D"), maxResultCount))
                .thenReturn(List.of(videoItemD));

        sut.collect(List.of("D", "D"));

        verify(videoFetcher, times(1)).fetch(List.of("D"), maxResultCount);
    }
}