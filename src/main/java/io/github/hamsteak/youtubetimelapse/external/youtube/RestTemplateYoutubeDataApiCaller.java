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

import static io.github.hamsteak.youtubetimelapse.config.Constants.*;

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
    public ChannelResponse fetchChannel(String channelYoutubeId) {
        String part = String.join(",", List.of("id", "snippet"));

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
    public List<ChannelListResponse> fetchChannels(List<String> channelYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet"));

        List<ChannelListResponse> responses = new ArrayList<>();
        int fetchCount = (channelYoutubeIds.size() - 1) / 50 + 1;
        String pageToken = "";

        for (int i=0; i<fetchCount; i++) {
            List<String> fetchIds = channelYoutubeIds.subList(i * MAX_FETCH_COUNT, Math.min((i + 1) * MAX_FETCH_COUNT, channelYoutubeIds.size()));

            String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/channels")
                    .queryParam("key", googleApiKey)
                    .queryParam("part", part)
                    .queryParam("id", String.join(",", fetchIds))
                    .queryParam("pageToken", pageToken)
                    .build().toString();

            ChannelListResponse response = restTemplate.getForObject(requestUrl, ChannelListResponse.class);

            if (response == null) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
            }

            responses.add(response);
            pageToken = response.getNextPageToken();
        }

        return responses;
    }

    @Override
    public VideoResponse fetchVideo(String videoYoutubeId) {
        String part = String.join(",", List.of("id", "snippet"));

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
    public List<VideoListResponse> fetchVideos(List<String> videoYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet"));

        List<VideoListResponse> responses = new ArrayList<>();
        int fetchCount = (videoYoutubeIds.size() - 1) / 50 + 1;
        String pageToken = "";

        for (int i=0; i<fetchCount; i++) {
            List<String> fetchIds = videoYoutubeIds.subList(i * MAX_FETCH_COUNT, Math.min((i + 1) * MAX_FETCH_COUNT, videoYoutubeIds.size()));

            String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/videos")
                    .queryParam("key", googleApiKey)
                    .queryParam("part", part)
                    .queryParam("id", String.join(",", fetchIds))
                    .queryParam("pageToken", pageToken)
                    .build().toString();

            VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

            if (response == null) {
                throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get video");
            }

            responses.add(response);
            pageToken = response.getNextPageToken();
        }

        return responses;
    }

    @Override
    public List<VideoListResponse> fetchTrendings(int count, String regionCode) {
        String part = "id";
        String chart = "mostPopular";

        List<VideoListResponse> responses = new ArrayList<>();

        int remainCount = count;
        String pageToken = "";
        while (remainCount > 0) {
            int maxResults = Math.min(remainCount, MAX_FETCH_COUNT);
            remainCount -= maxResults;

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

            if (response.getNextPageToken() == null) {
                break;
            }
        }

        return responses;
    }
}
