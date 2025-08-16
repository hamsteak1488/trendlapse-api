package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import io.github.hamsteak.trendlapse.common.errors.exception.YoutubeNullResponseException;
import io.github.hamsteak.trendlapse.external.youtube.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.RegionListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.external.youtube.dto.VideoListResponse;
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
@Timed("youtube.api.call")
@Retryable(maxAttempts = 20, retryFor = {RestClientException.class, YoutubeNullResponseException.class}, listeners = "retryWarnLogger")
@Component
@RequiredArgsConstructor
public class RestTemplateYoutubeDataApiCaller implements YoutubeDataApiCaller {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final RestTemplate restTemplate;

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
            throw new YoutubeNullResponseException("Response result of fetching channels is null.");
        }

        if (response.getItems() == null) {
            // 요청한 채널이 비공개 혹은 정지당한 채널일 경우 items가 null이 되는데, 이 때 NullPointerException을 예방하기 위해 빈 리스트가 들어있는 응답으로 대체.
            response = new ChannelListResponse(new ArrayList<>());
        }

        return response;
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
            throw new YoutubeNullResponseException("Response result of fetching videos is null.");
        }

        if (response.getItems() == null) {
            // 요청한 영상이 비공개 혹은 정지당한 영상일 경우 items가 null이 되는데, 이 때 NullPointerException을 예방하기 위해 빈 리스트가 들어있는 응답으로 대체.
            response = new VideoListResponse(new ArrayList<>());
        }

        return response;
    }

    @Override
    public TrendingListResponse fetchTrendings(int maxResultCount, String regionCode, String pageToken) {
        String part = "id";
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

        TrendingListResponse response = restTemplate.getForObject(requestUri, TrendingListResponse.class);

        if (response == null) {
            throw new YoutubeNullResponseException("Response result of fetching trendings is null.");
        }

        return response;
    }

    @Override
    public RegionListResponse fetchRegions() {
        String part = "snippet";

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/i18nRegions")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .build().toUri();

        RegionListResponse response = restTemplate.getForObject(requestUri, RegionListResponse.class);

        if (response == null) {
            throw new YoutubeNullResponseException("Response result of fetching regions is null.");
        }

        return response;
    }
}
