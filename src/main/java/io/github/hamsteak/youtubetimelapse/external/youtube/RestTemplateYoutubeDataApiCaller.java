package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.ChannelResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.VideoResponse;
import lombok.RequiredArgsConstructor;
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
        String requestUrl = String.format("%s/channels?key=%s&part=id,snippet&id=%s", baseUrl, googleApiKey, channelYoutubeId);

        ChannelListResponse response = restTemplate.getForObject(requestUrl, ChannelListResponse.class);

        // Check NullPointerException

        return response.getItems().get(0);
    }

    @Override
    public VideoResponse getVideo(String videoYoutubeId) {
        String requestUrl = String.format("%s/videos?key=%s&part=id,snippet&id=%s", baseUrl, googleApiKey, videoYoutubeId);
        VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

        // Check NullPointerException

        return response.getItems().get(0);
    }

    @Override
    public VideoListResponse getTrendings() {
        String requestUrl = String.format("%s/videos?key=%s&part=id&chart=mostPopular&maxResults=10&regionCode=kr", baseUrl, googleApiKey);
        VideoListResponse response = restTemplate.getForObject(requestUrl, VideoListResponse.class);

        // Check NullPointerException

        return response;
    }
}
