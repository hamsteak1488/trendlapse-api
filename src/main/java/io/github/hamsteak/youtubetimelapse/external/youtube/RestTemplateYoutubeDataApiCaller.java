package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.youtubetimelapse.common.errors.exception.RestApiException;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestTemplateYoutubeDataApiCaller implements YoutubeDataApiCaller {
    private final RestTemplate restTemplate;

    private final String baseUrl = "https://www.googleapis.com/youtube/v3";
    private final String googleApiKey;

    public RestTemplateYoutubeDataApiCaller(RestTemplate restTemplate, @Value("${google-api-key}") String googleApiKey) {
        this.restTemplate = restTemplate;
        this.googleApiKey = googleApiKey;
    }

    @Override
    public ChannelResponse getChannel(String channelYoutubeId) {
        String part = "id,snippet";

        String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/channels")
                .queryParam("key", googleApiKey)
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
    public VideoResponse getVideo(String videoYoutubeId) {
        String part = "id,snippet";

        String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/videos")
                .queryParam("key", googleApiKey)
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
    public List<VideoListResponse> getTrendings(int count) {
        List<VideoListResponse> responses = new ArrayList<>();

        int remainCount = count;
        String pageToken = "";
        while (remainCount > 0) {
            int maxResults = Math.min(remainCount, 50);
            remainCount -= maxResults;

            String part = "id";
            String regionCode = "kr";
            String chart = "mostPopular";

            String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/videos")
                    .queryParam("key", googleApiKey)
                    .queryParam("part", part)
                    .queryParam("chart", chart)
                    .queryParam("maxResults", maxResults)
                    .queryParam("regionCode", regionCode)
                    .queryParam("pageToken", pageToken)
                    .build().toString();

            VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

            if (response == null) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get trendings");
            }

            responses.add(response);
            pageToken = response.getNextPageToken();
        }

        return responses;
    }
}
