package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import io.github.hamsteak.trendlapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.trendlapse.common.errors.exception.RestApiException;
import io.github.hamsteak.trendlapse.external.youtube.dto.*;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Primary
@Timed("youtube.api.call")
@Component
@RequiredArgsConstructor
public class RestTemplateYoutubeDataApiCaller implements YoutubeDataApiCaller {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final RestTemplate restTemplate;

    @Override
    public ChannelResponse fetchChannel(String channelYoutubeId) {
        String part = String.join(",", List.of("id", "snippet"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/channels")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", channelYoutubeId)
                .build().toUri();

        ChannelListResponse response = restTemplate.getForObject(requestUri, ChannelListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
        }

        return response.getItems().get(0);
    }

    @Override
    public ChannelListResponse fetchChannels(List<String> channelYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/channels")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", channelYoutubeIds))
                .build().toUri();

        ChannelListResponse response = restTemplate.getForObject(requestUri, ChannelListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
        }

        return response;
    }

    @Override
    public VideoResponse fetchVideo(String videoYoutubeId) {
        String part = String.join(",", List.of("id", "snippet"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", videoYoutubeId)
                .build().toUri();

        VideoListResponse response = restTemplate.getForObject(requestUri, VideoListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get video");
        }

        return response.getItems().get(0);
    }

    @Override
    public VideoListResponse fetchVideos(List<String> videoYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", videoYoutubeIds))
                .build().toUri();

        VideoListResponse response = restTemplate.getForObject(requestUri, VideoListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get video");
        }

        return response;
    }

    @Override
    public TrendingListResponse fetchTrendings(int count, String regionCode, String pageToken) {
        String part = "id";
        String chart = "mostPopular";

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("chart", chart)
                .queryParam("maxResults", count)
                .queryParam("regionCode", regionCode)
                .queryParam("pageToken", pageToken)
                .build().toUri();

        TrendingListResponse response = restTemplate.getForObject(requestUri, TrendingListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get trendings");
        }

        return response;
    }
}
