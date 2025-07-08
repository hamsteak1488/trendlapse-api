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
        String requestUrl = String.format("%s/channels?key=%s&part=%s&id=%s", baseUrl, googleApiKey, part, channelYoutubeId);

        ChannelListResponse response = restTemplate.getForObject(requestUrl, ChannelListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
        }

        return response.getItems().get(0);
    }

    @Override
    public VideoResponse getVideo(String videoYoutubeId) {
        String part = "id,snippet";
        String requestUrl = String.format("%s/videos?key=%s&part=%s&id=%s", baseUrl, googleApiKey, part, videoYoutubeId);
        VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get video");
        }

        return response.getItems().get(0);
    }

    @Override
    public VideoListResponse getTrendings() {
        String part = "id";
        int maxResults = 10;
        String requestUrl = String.format("%s/videos?key=%s&part=%s&chart=mostPopular&maxResults=%s&regionCode=kr", baseUrl, googleApiKey, part, maxResults);
        VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get trendings");
        }

        return response;
    }
}
