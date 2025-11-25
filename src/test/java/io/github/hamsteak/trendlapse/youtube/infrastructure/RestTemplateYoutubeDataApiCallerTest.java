package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.ChannelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestTemplateYoutubeDataApiCallerTest {
    @Mock
    private RestTemplate restTemplate;

    private RestTemplateYoutubeDataApiCaller apiCaller;

    @BeforeEach
    void setup() {
        apiCaller = new RestTemplateYoutubeDataApiCaller(
                new YoutubeDataApiProperties(
                        "https://www.googleapis.com/youtube/v3",
                        "fake-api-key",
                        50,
                        false,
                        0,
                        10
                ),
                restTemplate
        );
    }

    @Test
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
    void givenValidId_whenFetchChannel_thenReturnChannel() {
        // Given
        String channelId = "test-channel-id";
        String channelTitle = "test-channel-title";
        ChannelResponse mockResponse = new ChannelResponse(
                channelId,
                new ChannelResponse.Snippet(
                        channelTitle,
                        new ChannelResponse.Snippet.Thumbnails(
                                new ChannelResponse.Snippet.Thumbnails.Thumbnail("test-thumbnail-url")
                        )
                )
        );
        ChannelListResponse mockListResponse = new ChannelListResponse(List.of(mockResponse));

//        when(restTemplate.getForObject(any(), any())).thenReturn(mockListResponse);
        when(restTemplate.getForObject(any(URI.class), eq(ChannelListResponse.class)))
                .thenReturn(mockListResponse);

        // When
        ChannelResponse result = apiCaller.fetchChannels(List.of(channelId)).getItems().get(0);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockResponse);
    }

    @Test
    void fetchVideo() {
    }

    @Test
    void fetchTrendings() {
    }
}