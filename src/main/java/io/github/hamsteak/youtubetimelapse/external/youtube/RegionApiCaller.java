package io.github.hamsteak.youtubetimelapse.external.youtube;

import io.github.hamsteak.youtubetimelapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.youtubetimelapse.common.errors.exception.RestApiException;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.RegionListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class RegionApiCaller {
    private final RestTemplate restTemplate;

    private final String baseUrl = "https://www.googleapis.com/youtube/v3";
    private final String googleApiKey;

    public RegionApiCaller(RestTemplate restTemplate, @Value("${google-api-key}") String googleApiKey) {
        this.restTemplate = restTemplate;
        this.googleApiKey = googleApiKey;
    }

    public RegionListResponse fetchRegions() {
        String part = "snippet";

        String requestUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/i18nRegions")
                .queryParam("key", googleApiKey)
                .queryParam("part", part)
                .build().toString();

        RegionListResponse response = restTemplate.getForObject(requestUrl, RegionListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
        }

        return response;
    }
}
