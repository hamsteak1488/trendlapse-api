package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestTemplateYoutubeDataApiCallerTest {
    @Mock
    private RestTemplate restTemplate;

    private RestTemplateYoutubeDataApiCaller caller;

    @BeforeEach
    void setup() {
        caller = new RestTemplateYoutubeDataApiCaller(restTemplate, "fake-api-key");
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
                new ChannelResponse.Snippet(channelTitle, new ChannelResponse.Snippet.Thumbnails(new ChannelResponse.Snippet.Thumbnails.Thumbnail("test-thumbnail-url")))
        );
        ChannelListResponse mockListResponse = new ChannelListResponse(List.of(mockResponse), "test-page-token");

        when(restTemplate.getForObject(anyString(), eq(ChannelListResponse.class)))
                .thenReturn(mockListResponse);

        // When
        ChannelResponse result = caller.fetchChannel(channelId);

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