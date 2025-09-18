package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.collector.domain.TrendingItem;
import io.github.hamsteak.trendlapse.collector.domain.VideoCollector;
import io.github.hamsteak.trendlapse.collector.fetcher.TrendingFetcher;
import io.github.hamsteak.trendlapse.collector.storer.TrendingStorer;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OneByOneTrendingCollectorTest {
    @Mock
    TrendingFetcher trendingFetcher;
    @Mock
    TrendingStorer trendingStorer;
    @Mock
    VideoCollector videoCollector;
    @InjectMocks
    OneByOneTrendingCollector sut;

    // --- 헬퍼: 간단한 응답 객체 생성 ---
    private VideoResponse video(String id, String channelId, String title, String thumbUrl) {
        VideoResponse.Snippet.Thumbnails.Thumbnail high = new VideoResponse.Snippet.Thumbnails.Thumbnail(thumbUrl);

        VideoResponse.Snippet.Thumbnails thumbs = new VideoResponse.Snippet.Thumbnails(high);

        VideoResponse.Snippet snippet = new VideoResponse.Snippet(title, channelId, thumbs);

        VideoResponse resp = new VideoResponse(id, snippet);

        return resp;
    }

    private TrendingListResponse list(String nextPageToken, VideoResponse... items) {
        TrendingListResponse list = new TrendingListResponse(List.of(items), nextPageToken);

        return list;
    }

    // 1) 매핑 정확성
    @Test
    void collect_maps_response_to_entity_fields() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(1, 1, 1, 1, 1);

        TrendingItem trendingItem = new TrendingItem(dateTime, "RG1", 1, "video-youtube-id");
        when(trendingFetcher.fetch(dateTime, 1, List.of("RG1")))
                .thenReturn(List.of(trendingItem));

        ArgumentCaptor<LocalDateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<String> videoYoutubeIdCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> rankCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> regionCodeCapture = ArgumentCaptor.forClass(String.class);

        // when
        sut.collect(dateTime, 1, List.of("RG1"));

        // then
        verify(trendingStorer, times(1)).store(List.of(trendingItem));
    }
}