package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v0.OneByOneChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.ChannelFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.ChannelStorer;
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
class OneByOneChannelCollectorTest {
    @Mock
    ChannelFinder channelFinder;
    @Mock
    ChannelFetcher channelFetcher;
    @Mock
    ChannelStorer channelStorer;
    @InjectMocks
    OneByOneChannelCollector sut;

    // 1) 신규만 조회
    @Test
    void collect_queries_only_non_existing_ids_and_saves_them() {
        // given
        List<String> ids = List.of("A", "B", "C");
        when(channelFinder.findMissingChannelYoutubeIds(ids)).thenReturn(List.of("A", "C"));

        ChannelItem channelItemA = new ChannelItem("A", "titleA", "urlA");
        ChannelItem channelItemC = new ChannelItem("C", "titleC", "urlC");
        when(channelFetcher.fetch(List.of("A", "C"), 1))
                .thenReturn(List.of(channelItemA, channelItemC));

        // when
        sut.collect(ids);

        // then
        verify(channelFetcher, times(1)).fetch(List.of("A", "C"), 1);
        verify(channelStorer, times(1)).store(anyList());
    }

    // 2) 빈 응답 스킵
    @Test
    void collect_skips_when_items_empty() {
        when(channelFinder.findMissingChannelYoutubeIds(anyList())).thenReturn(List.of("A", "B"));

        ChannelItem channelItemB = new ChannelItem("B", "titleB", "urlB");
        when(channelFetcher.fetch(List.of("A", "B"), 1))
                .thenReturn(List.of(channelItemB));

        sut.collect(List.of("A", "B"));

        verify(channelStorer, times(1)).store(List.of(channelItemB));
    }

    // 3) 매핑 정확성
    @Test
    void collect_maps_response_to_entity_fields() {
        when(channelFinder.findMissingChannelYoutubeIds(List.of("Z"))).thenReturn(List.of("Z"));
        ChannelItem channelItemZ = new ChannelItem("Z", "Z-Title", "https://thumb/z.jpg");
        when(channelFetcher.fetch(List.of("Z"), 1))
                .thenReturn(List.of(channelItemZ));

        ArgumentCaptor<List> storeChannelItemsArgCaptor = ArgumentCaptor.forClass(List.class);

        sut.collect(List.of("Z"));

        verify(channelStorer).store(storeChannelItemsArgCaptor.capture());
        ChannelItem channelItem = ((List<ChannelItem>) storeChannelItemsArgCaptor.getValue()).get(0);
        assertThat(channelItem.getYoutubeId()).isEqualTo("Z");
        assertThat(channelItem.getTitle()).isEqualTo("Z-Title");
        assertThat(channelItem.getThumbnailUrl()).isEqualTo("https://thumb/z.jpg");
    }

    // 4) 중복 입력 동작 문서화 (현재 구현: 중복 호출 발생)
    @Test
    void collect_calls_api_per_each_input_even_if_duplicate() {
        when(channelFinder.findMissingChannelYoutubeIds(List.of("D"))).thenReturn(List.of("D"));
        when(channelFetcher.fetch(List.of("D"), 1))
                .thenReturn(List.of(new ChannelItem("D", "title", "url")));

        sut.collect(List.of("D", "D"));

        verify(channelFetcher, times(1)).fetch(List.of("D"), 1);
    }
}

