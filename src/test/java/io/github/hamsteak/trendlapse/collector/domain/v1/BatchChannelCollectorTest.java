package io.github.hamsteak.trendlapse.collector.domain.v1;

import io.github.hamsteak.trendlapse.channel.application.component.ChannelFinder;
import io.github.hamsteak.trendlapse.collector.application.component.collector.v1.BatchChannelCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.ChannelFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.ChannelStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.ChannelItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiProperties;
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
    ChannelFinder channelFinder;
    @Mock
    ChannelFetcher channelFetcher;
    @Mock
    ChannelStorer channelStorer;
    @Mock
    YoutubeDataApiProperties youtubeDataApiProperties;
    @InjectMocks
    BatchChannelCollector sut;


    // 1) 신규만 조회
    @Test
    void collect_queries_only_non_existing_ids_and_saves_them() {
        // given
        ChannelItem channelItemA = new ChannelItem("A", "titleA", "urlA");
        ChannelItem channelItemC = new ChannelItem("C", "titleC", "urlC");
        int maxResultCount = 3;
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);

        List<String> ids = List.of("A", "B", "C");
        when(channelFinder.findMissingChannelYoutubeIds(ids)).thenReturn(List.of("A", "C"));

        when(channelFetcher.fetch(List.of("A", "C"), maxResultCount))
                .thenReturn(List.of(channelItemA, channelItemC));

        // when
        sut.collect(ids);

        // then
        verify(channelFetcher, times(1)).fetch(List.of("A", "C"), maxResultCount);
        verify(channelFetcher, never()).fetch(List.of("A", "B", "C"), maxResultCount);
        verify(channelStorer, times(1)).store(List.of(channelItemA, channelItemC));
    }

    // 2) 빈 응답 스킵
    @Test
    void collect_skips_when_items_empty() {
        // given
        ChannelItem channelItemB = new ChannelItem("B", "titleB", "urlB");
        int maxResultCount = 3;
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);

        when(channelFinder.findMissingChannelYoutubeIds(anyList())).thenReturn(List.of("A", "B"));

        when(channelFetcher.fetch(List.of("A", "B"), maxResultCount))
                .thenReturn(List.of(channelItemB)); // empty

        // when
        sut.collect(List.of("A", "B"));

        // then
        verify(channelStorer, times(1)).store(List.of(channelItemB));
    }

    // 3) 매핑 정확성
    @Test
    void collect_maps_response_to_entity_fields() {
        // given
        ChannelItem channelItemZ = new ChannelItem("Z", "titleZ", "urlZ");
        int maxResultCount = 3;
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);
        when(channelFinder.findMissingChannelYoutubeIds(List.of("Z"))).thenReturn(List.of("Z"));
        when(channelFetcher.fetch(List.of("Z"), maxResultCount))
                .thenReturn(List.of(channelItemZ));

        ArgumentCaptor<List> storeChannelItemsArgCaptor = ArgumentCaptor.forClass(List.class);

        // when
        sut.collect(List.of("Z"));

        // then
        verify(channelStorer).store(storeChannelItemsArgCaptor.capture());
        ChannelItem channelItem = ((List<ChannelItem>) storeChannelItemsArgCaptor.getValue()).get(0);
        assertThat(channelItem.getYoutubeId()).isEqualTo("Z");
        assertThat(channelItem.getTitle()).isEqualTo("titleZ");
        assertThat(channelItem.getThumbnailUrl()).isEqualTo("urlZ");
    }

    // 4) 중복 입력 동작
    @Test
    void collect_calls_api_per_each_input_even_if_duplicate() {
        // given
        int maxResultCount = 3;
        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);
        when(channelFinder.findMissingChannelYoutubeIds(List.of("D"))).thenReturn(List.of("D"));
        when(channelFetcher.fetch(List.of("D"), maxResultCount))
                .thenReturn(List.of(new ChannelItem("D", "title", "url")));

        // when
        sut.collect(List.of("D", "D"));

        // then
        verify(channelFetcher, times(1)).fetch(List.of("D"), maxResultCount);
    }
}

