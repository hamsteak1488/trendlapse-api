package io.github.hamsteak.trendlapse.collector.domain.v0;

import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.YoutubeDataApiProperties;
import io.github.hamsteak.trendlapse.trending.domain.TrendingCreator;
import io.github.hamsteak.trendlapse.video.domain.VideoReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OneByOneTrendingCollectorTest {
    @Mock
    YoutubeDataApiCaller youtubeDataApiCaller;
    @Mock
    YoutubeDataApiProperties youtubeDataApiProperties;
    @Mock
    OneByOneVideoCollector oneByOneVideoCollector;
    @Mock
    TrendingCreator trendingCreator;
    @Mock
    VideoReader videoReader;
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
        int maxResultCount = 0;

        when(youtubeDataApiProperties.getMaxResultCount()).thenReturn(maxResultCount);
        when(youtubeDataApiCaller.fetchTrendings(maxResultCount, "RG1", null))
                .thenReturn(list(null, video("Z", "channelZ", "Z-Title", "https://thumb/z.jpg")));

        ArgumentCaptor<LocalDateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<String> videoYoutubeIdCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> rankCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> regionCodeCapture = ArgumentCaptor.forClass(String.class);

        // when
        int saved = sut.collect(dateTime, 1, List.of("RG1"));

        // then
        verify(trendingCreator).create(dateTimeArgumentCaptor.capture(), videoYoutubeIdCapture.capture(), rankCapture.capture(), regionCodeCapture.capture());
        assertThat(dateTimeArgumentCaptor.getValue()).isEqualTo(dateTime);
        assertThat(videoYoutubeIdCapture.getValue()).isEqualTo("Z");
        assertThat(rankCapture.getValue()).isEqualTo(1);
        assertThat(regionCodeCapture.getValue()).isEqualTo("RG1");
        assertThat(saved).isEqualTo(1);
    }
}