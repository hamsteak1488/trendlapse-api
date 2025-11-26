package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.ChannelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestTemplateYoutubeDataApiCallerTest {
    @Mock
    private RestTemplate restTemplate;

    private YoutubeDataApiProperties properties;
    private RestTemplateYoutubeDataApiCaller apiCaller;

    @BeforeEach
    void setup() {
        properties = new YoutubeDataApiProperties(
                "https://www.googleapis.com/youtube/v3",
                "fake-api-key",
                50,
                false,
                0,
                10
        );
        apiCaller = new RestTemplateYoutubeDataApiCaller(
                properties,
                restTemplate
        );
    }

    @Test
    @DisplayName("URL 생성 로직 확인 (참고용)")
    void urlTest() {
        String requestUrl = UriComponentsBuilder.fromUriString("https://www.googleapis.com/youtube/v3")
                .path("/channels")
                .queryParam("key", "google-api-key")
                .queryParam("part", "id,snippet")
                .queryParam("id", 1)
                .build().toString();
        assertThat(requestUrl).isEqualTo("https://www.googleapis.com/youtube/v3/channels?key=google-api-key&part=id,snippet&id=1");
    }

    @Test
    @DisplayName("채널 정보를 요청할 때, 올바른 URI로 요청하고 결과를 반환한다")
    void givenValidId_whenFetchChannel_thenReturnChannel() {
        // given
        String channelId = "test-channel-id";
        ChannelResponse mockResponse = new ChannelResponse(
                channelId,
                new ChannelResponse.Snippet(
                        "test-channel-title",
                        new ChannelResponse.Snippet.Thumbnails(
                                new ChannelResponse.Snippet.Thumbnails.Thumbnail("test-thumbnail-url")
                        )
                )
        );
        ChannelListResponse mockListResponse = new ChannelListResponse(List.of(mockResponse));

        when(restTemplate.getForObject(any(URI.class), eq(ChannelListResponse.class)))
                .thenReturn(mockListResponse);

        // when
        ChannelListResponse result = apiCaller.fetchChannels(List.of(channelId));

        // then
        // 1. 결과값 검증
        assertThat(result.getItems()).isNotNull();
        assertThat(result.getItems().get(0)).isEqualTo(mockResponse);

        // 2. 행위 검증 (ArgumentCaptor 사용)
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        verify(restTemplate).getForObject(uriCaptor.capture(), eq(ChannelListResponse.class));

        URI actualUri = uriCaptor.getValue();
        assertThat(actualUri.toString()).contains(properties.getBaseUrl());
        assertThat(actualUri.toString()).contains("key=" + properties.getApiKey());
        assertThat(actualUri.toString()).contains("part=id,snippet");
        assertThat(actualUri.toString()).contains("id=" + channelId);
    }

    @Test
    void fetchVideo() {
    }

    @Test
    void fetchTrendings() {
    }
}
