package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.application.YoutubeApiClient;
import io.github.hamsteak.trendlapse.youtube.domain.YoutubeNullResponseException;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawRegionListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawVideoListResponse;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@Retryable(maxAttemptsExpression = "${youtube-data-api.retry-count}",
        retryFor = {RestClientException.class, YoutubeNullResponseException.class},
        listeners = "retryWarnLogger")
@Timed("youtube.api.call")
@RequiredArgsConstructor
public class RestTemplateYoutubeApiClient implements YoutubeApiClient {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final RestTemplate restTemplate;

    @Override
    public RawChannelListResponse fetchChannels(List<String> channelYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet, statistics"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/channels")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", channelYoutubeIds))
                .build().toUri();

        RawChannelListResponse response = restTemplate.getForObject(requestUri, RawChannelListResponse.class);

        if (response == null) {
            throw new YoutubeNullResponseException("Response result of fetching channels is null.");
        }

        if (response.getItems() == null) {
            // 요청한 채널이 비공개 혹은 정지당한 채널일 경우 items가 null이 되는데, 이 때 NullPointerException을 예방하기 위해 빈 리스트가 들어있는 응답으로 대체.
            response = new RawChannelListResponse(new ArrayList<>());
        }

        return response;
    }

    @Override
    public RawVideoListResponse fetchVideos(List<String> videoYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet, statistics"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", videoYoutubeIds))
                .build().toUri();

        RawVideoListResponse response = restTemplate.getForObject(requestUri, RawVideoListResponse.class);

        if (response == null) {
            throw new YoutubeNullResponseException("Response result of fetching videos is null.");
        }

        if (response.getItems() == null) {
            // 요청한 영상이 비공개 혹은 정지당한 영상일 경우 items가 null이 되는데, 이 때 NullPointerException을 예방하기 위해 빈 리스트가 들어있는 응답으로 대체.
            response = new RawVideoListResponse(new ArrayList<>(), null);
        }

        return response;
    }

    @Override
    public RawVideoListResponse fetchTrendings(String regionCode, String pageToken, int maxResultCount) {
        String part = String.join(",", List.of("id", "snippet, statistics"));
        String chart = "mostPopular";

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("chart", chart)
                .queryParam("maxResults", maxResultCount)
                .queryParam("regionCode", regionCode)
                .queryParam("pageToken", pageToken)
                .build().toUri();

        RawVideoListResponse response = restTemplate.getForObject(requestUri, RawVideoListResponse.class);

        if (response == null) {
            throw new YoutubeNullResponseException("Response result of fetching trendings is null.");
        }

        return response;
    }

    @Override
    public RawRegionListResponse fetchRegions() {
        String part = "snippet";

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/i18nRegions")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .build().toUri();

        RawRegionListResponse response = restTemplate.getForObject(requestUri, RawRegionListResponse.class);

        if (response == null) {
            throw new YoutubeNullResponseException("Response result of fetching regions is null.");
        }

        return response;
    }
}
