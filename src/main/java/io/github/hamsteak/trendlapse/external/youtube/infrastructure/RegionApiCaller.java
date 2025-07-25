package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import io.github.hamsteak.trendlapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.trendlapse.common.errors.exception.RestApiException;
import io.github.hamsteak.trendlapse.external.youtube.dto.RegionListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegionApiCaller {
    private final YoutubeDataApiProperties properties;
    private final RestTemplate restTemplate;

    public RegionListResponse fetchRegions() {
        String part = "snippet";

        String requestUrl = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path("/i18nRegions")
                .queryParam("key", properties.getApiKey())
                .queryParam("part", part)
                .build().toString();

        RegionListResponse response = restTemplate.getForObject(requestUrl, RegionListResponse.class);

        if (response == null) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR, "Failed to get channel");
        }

        return response;
    }
}
