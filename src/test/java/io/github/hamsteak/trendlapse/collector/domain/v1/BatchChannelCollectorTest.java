package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.channel.domain.ChannelCreator;
import io.github.hamsteak.trendlapse.channel.domain.ChannelFinder;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
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
class BatchChannelCollectorTest {
    @Mock
    YoutubeDataApiProperties youtubeDataApiProperties;
    @Mock
    YoutubeDataApiCaller youtubeDataApiCaller;
    @Mock
    ChannelCreator channelCreator;
    @Mock
    ChannelFinder channelFinder;
    @InjectMocks
    BatchChannelCollector sut;

    // --- 헬퍼: 간단한 응답 객체 생성 ---
    private ChannelResponse channel(String id, String title, String thumbUrl) {
        ChannelResponse.Snippet.Thumbnails.Thumbnail high = new ChannelResponse.Snippet.Thumbnails.Thumbnail(thumbUrl);

        ChannelResponse.Snippet.Thumbnails thumbs = new ChannelResponse.Snippet.Thumbnails(high);

        ChannelResponse.Snippet snippet = new ChannelResponse.Snippet(title, thumbs);

        ChannelResponse resp = new ChannelResponse(id, snippet);

        return resp;
    }

    private ChannelListResponse list(ChannelResponse... items) {
        ChannelListResponse list = new ChannelListResponse(List.of(items));

        return list;
    }

    // 1) 신규만 조회
    @Test
    void collect_queries_only_non_existing_ids_and_saves_them() {
        // given
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(3);

        List<String> ids = List.of("A", "B", "C");
        when(channelFinder.findMissingChannelYoutubeIds(ids)).thenReturn(List.of("A", "C"));

        when(youtubeDataApiCaller.fetchChannels(List.of("A", "C")))
                .thenReturn(list(channel("A", "titleA", "urlA"), channel("C", "titleC", "urlC")));

        // when
        int saved = sut.collect(ids);

        // then
        verify(youtubeDataApiCaller, times(1)).fetchChannels(List.of("A", "C"));
        verify(youtubeDataApiCaller, never()).fetchChannels(List.of("A", "B", "C"));
        verify(channelCreator, times(2)).create(any(), any(), any());
        assertThat(saved).isEqualTo(2);
    }

    // 2) 빈 응답 스킵
    @Test
    void collect_skips_when_items_empty() {
        // given
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(3);

        when(channelFinder.findMissingChannelYoutubeIds(anyList())).thenReturn(List.of("A", "B"));

        when(youtubeDataApiCaller.fetchChannels(List.of("A", "B")))
                .thenReturn(list(channel("B", "titleB", "urlB"))); // empty

        // when
        int saved = sut.collect(List.of("A", "B"));

        // then
        verify(channelCreator, times(1)).create(any(), any(), any());
        assertThat(saved).isEqualTo(1);
    }

    // 3) 매핑 정확성
    @Test
    void collect_maps_response_to_entity_fields() {
        // given
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(3);
        when(channelFinder.findMissingChannelYoutubeIds(List.of("Z"))).thenReturn(List.of("Z"));
        when(youtubeDataApiCaller.fetchChannels(List.of("Z")))
                .thenReturn(list(channel("Z", "Z-Title", "https://thumb/z.jpg")));

        ArgumentCaptor<String> youtubeIdCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> thumbnailUrlCapture = ArgumentCaptor.forClass(String.class);

        // when
        int saved = sut.collect(List.of("Z"));

        // then
        verify(channelCreator).create(youtubeIdCapture.capture(), titleCapture.capture(), thumbnailUrlCapture.capture());
        assertThat(youtubeIdCapture.getValue()).isEqualTo("Z");
        assertThat(titleCapture.getValue()).isEqualTo("Z-Title");
        assertThat(thumbnailUrlCapture.getValue()).isEqualTo("https://thumb/z.jpg");
        assertThat(saved).isEqualTo(1);
    }

    // 4) 중복 입력 동작
    @Test
    void collect_calls_api_per_each_input_even_if_duplicate() {
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(3);
        when(channelFinder.findMissingChannelYoutubeIds(List.of("D"))).thenReturn(List.of("D"));
        when(youtubeDataApiCaller.fetchChannels(List.of("D")))
                .thenReturn(list(channel("D", "title", "url")));

        sut.collect(List.of("D", "D"));

        verify(youtubeDataApiCaller, times(1)).fetchChannels(List.of("D"));
    }

    // 5) 이미 존재하면 API 미호출
    @Test
    void collect_does_not_call_api_if_exists() {
        when(channelFinder.findMissingChannelYoutubeIds(List.of("E"))).thenReturn(List.of());

        sut.collect(List.of("E"));

        verifyNoInteractions(youtubeDataApiCaller);
        verify(channelCreator, never()).create(any(), any(), any());
    }
}

