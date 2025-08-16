package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.video.domain.VideoCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneByOneVideoCollectorTest {
    @Mock
    YoutubeDataApiCaller youtubeDataApiCaller;
    @Mock
    VideoFinder videoFinder;
    @Mock
    VideoCreator videoCreator;
    @Mock
    OneByOneChannelCollector oneByOneChannelCollector;
    @InjectMocks
    OneByOneVideoCollector sut;

    // --- 헬퍼: 간단한 응답 객체 생성 ---
    private VideoResponse video(String id, String channelId, String title, String thumbUrl) {
        VideoResponse.Snippet.Thumbnails.Thumbnail high = new VideoResponse.Snippet.Thumbnails.Thumbnail(thumbUrl);

        VideoResponse.Snippet.Thumbnails thumbs = new VideoResponse.Snippet.Thumbnails(high);

        VideoResponse.Snippet snippet = new VideoResponse.Snippet(title, channelId, thumbs);

        VideoResponse resp = new VideoResponse(id, snippet);

        return resp;
    }

    private VideoListResponse list(VideoResponse... items) {
        VideoListResponse list = new VideoListResponse(List.of(items));

        return list;
    }

    // 1) 신규만 조회
    @Test
    void collect_queries_only_non_existing_ids_and_saves_them() {
        // given
        List<String> ids = List.of("A", "B", "C");
        when(videoFinder.findMissingVideoYoutubeIds(ids)).thenReturn(List.of("A", "C"));

        when(youtubeDataApiCaller.fetchVideos(List.of("A")))
                .thenReturn(list(video("A", "channelA", "titleA", "urlA")));
        when(youtubeDataApiCaller.fetchVideos(List.of("C")))
                .thenReturn(list(video("C", "channelC", "titleC", "urlC")));

        // when
        int saved = sut.collect(ids);

        // then
        verify(youtubeDataApiCaller, times(1)).fetchVideos(List.of("A"));
        verify(youtubeDataApiCaller, never()).fetchVideos(List.of("B"));
        verify(youtubeDataApiCaller, times(1)).fetchVideos(List.of("C"));
        verify(oneByOneChannelCollector).collect(List.of("channelA", "channelC"));
        verify(videoCreator, times(2)).create(anyString(), anyString(), anyString(), anyString());
        assertThat(saved).isEqualTo(2);
    }

    // 2) 빈 응답 스킵
    @Test
    void collect_skips_when_items_empty() {
        // given
        when(videoFinder.findMissingVideoYoutubeIds(anyList())).thenReturn(List.of("A", "B"));

        when(youtubeDataApiCaller.fetchVideos(List.of("A")))
                .thenReturn(list()); // empty
        when(youtubeDataApiCaller.fetchVideos(List.of("B")))
                .thenReturn(list(video("B", "channelB", "titleB", "urlB")));

        // when
        int saved = sut.collect(List.of("A", "B"));

        // then
        verify(videoCreator, times(1)).create(anyString(), anyString(), anyString(), anyString());
        assertThat(saved).isEqualTo(1);
    }

    // 3) 매핑 정확성
    @Test
    void collect_maps_response_to_entity_fields() {
        when(videoFinder.findMissingVideoYoutubeIds(List.of("Z"))).thenReturn(List.of("Z"));
        when(youtubeDataApiCaller.fetchVideos(List.of("Z")))
                .thenReturn(list(video("Z", "channelZ", "Z-Title", "https://thumb/z.jpg")));

        ArgumentCaptor<String> youtubeIdCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> channelYoutubeIdCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> thumbnailUrlCapture = ArgumentCaptor.forClass(String.class);

        int saved = sut.collect(List.of("Z"));

        verify(videoCreator).create(youtubeIdCapture.capture(), channelYoutubeIdCapture.capture(), titleCapture.capture(), thumbnailUrlCapture.capture());
        assertThat(youtubeIdCapture.getValue()).isEqualTo("Z");
        assertThat(channelYoutubeIdCapture.getValue()).isEqualTo("channelZ");
        assertThat(titleCapture.getValue()).isEqualTo("Z-Title");
        assertThat(thumbnailUrlCapture.getValue()).isEqualTo("https://thumb/z.jpg");
        assertThat(saved).isEqualTo(1);
    }

    // 4) 중복 입력 동작 문서화 (현재 구현: 중복 호출 발생)
    @Test
    void collect_calls_api_per_each_input_even_if_duplicate() {
        when(videoFinder.findMissingVideoYoutubeIds(List.of("D"))).thenReturn(List.of("D"));
        when(youtubeDataApiCaller.fetchVideos(List.of("D")))
                .thenReturn(list(video("D", "channelD", "title", "url")));

        sut.collect(List.of("D", "D"));

        verify(youtubeDataApiCaller, times(1)).fetchVideos(List.of("D"));
    }

    // 5) 이미 존재하면 API 미호출
    @Test
    void collect_does_not_call_api_if_exists() {
        when(videoFinder.findMissingVideoYoutubeIds(List.of("E"))).thenReturn(List.of());

        sut.collect(List.of("E"));

        verifyNoInteractions(youtubeDataApiCaller);
        verify(videoCreator, never()).create(any(), anyString(), any(), any());
    }
}