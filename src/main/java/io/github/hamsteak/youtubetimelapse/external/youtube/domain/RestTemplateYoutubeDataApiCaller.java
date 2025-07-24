package io.github.hamsteak.youtubetimelapse.external.youtube.domain;

import io.github.hamsteak.youtubetimelapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.youtubetimelapse.common.errors.exception.RestApiException;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestTemplateYoutubeDataApiCaller implements YoutubeDataApiCaller {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final RestTemplate restTemplate;

    @Override
    public ChannelResponse fetchChannel(String channelYoutubeId) {
        String part = String.join(",", List.of("id", "snippet"));

        String requestUrl = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/channels")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", channelYoutubeId)
                .build().toString();

        ChannelListResponse response = restTemplate.getForObject(requestUrl, ChannelListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
        }

        return response.getItems().get(0);
    }

    @Override
    public ChannelListResponse fetchChannels(List<String> channelYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet"));

        String requestUrl = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/channels")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", channelYoutubeIds))
                .build().toString();

        ChannelListResponse response = restTemplate.getForObject(requestUrl, ChannelListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
        }

        return response;
    }

    @Override
    public VideoResponse fetchVideo(String videoYoutubeId) {
        String part = String.join(",", List.of("id", "snippet"));

        String requestUrl = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", videoYoutubeId)
                .build().toString();

        VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get video");
        }

        return response.getItems().get(0);
    }

    @Override
    public VideoListResponse fetchVideos(List<String> videoYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet"));

        String requestUrl = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", videoYoutubeIds))
                .build().toString();

        VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get video");
        }

        return response;
    }

    @Override
    public VideoListResponse fetchTrendings(int count, String regionCode, String pageToken) {
        String part = "id";
        String chart = "mostPopular";

        String requestUrl = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("chart", chart)
                .queryParam("maxResults", count)
                .queryParam("regionCode", regionCode)
                .queryParam("pageToken", pageToken)
                .build().toString();

        VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get trendings");
        }

        return response;
    }
}
