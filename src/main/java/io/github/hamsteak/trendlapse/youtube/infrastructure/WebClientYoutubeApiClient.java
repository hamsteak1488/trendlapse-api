package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.application.NonblockingYoutubeApiClient;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawRegionListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RawVideoListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebClientYoutubeApiClient implements NonblockingYoutubeApiClient {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final WebClient webClient;

    @Override
    public Mono<RawChannelListResponse> fetchChannels(List<String> channelYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet, statistics"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/channels")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", channelYoutubeIds))
                .build().toUri();

        return webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(RawChannelListResponse.class)
                .timeout(Duration.ofSeconds(youtubeDataApiProperties.getTimeout()))
                .retryWhen(Retry.backoff(youtubeDataApiProperties.getRetryCount(), Duration.ofSeconds(1))
                        .doBeforeRetry(retrySignal -> {
                            Throwable failure = retrySignal.failure();
                            long attempt = retrySignal.totalRetries() + 1;
                            log.warn("[WebClient] Retry #{} for fetching trendings (cause={})", attempt, failure.toString());
                        }))
                .flatMap(rawChannelListResponse -> {
                    // 요청한 채널이 비공개 혹은 정지당한 채널일 경우 items가 null이 되는데, 이 때 NullPointerException을 예방하기 위해 빈 리스트가 들어있는 응답으로 대체.
                    if (rawChannelListResponse.getItems() == null) {
                        return Mono.just(new RawChannelListResponse(new ArrayList<>()));
                    } else {
                        return Mono.just(rawChannelListResponse);
                    }
                });
    }

    @Override
    public Mono<RawVideoListResponse> fetchVideos(List<String> videoYoutubeIds) {
        String part = String.join(",", List.of("id", "snippet, statistics"));

        URI requestUri = UriComponentsBuilder.fromUriString(youtubeDataApiProperties.getBaseUrl())
                .path("/videos")
                .queryParam("key", youtubeDataApiProperties.getApiKey())
                .queryParam("part", part)
                .queryParam("id", String.join(",", videoYoutubeIds))
                .build().toUri();

        return webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(RawVideoListResponse.class)
                .timeout(Duration.ofSeconds(youtubeDataApiProperties.getTimeout()))
                .retryWhen(Retry.backoff(youtubeDataApiProperties.getRetryCount(), Duration.ofSeconds(1))
                        .doBeforeRetry(retrySignal -> {
                            Throwable failure = retrySignal.failure();
                            long attempt = retrySignal.totalRetries() + 1;
                            log.warn("[WebClient] Retry #{} for fetching trendings (cause={})", attempt, failure.toString());
                        }))
                .flatMap(rawVideoListResponse -> {
                    // 요청한 영상이 비공개 혹은 정지당한 영상일 경우 items가 null이 되는데, 이 때 NullPointerException을 예방하기 위해 빈 리스트가 들어있는 응답으로 대체.
                    if (rawVideoListResponse.getItems() == null) {
                        return Mono.just(new RawVideoListResponse(new ArrayList<>(), null));
                    } else {
                        return Mono.just(rawVideoListResponse);
                    }
                });
    }

    @Override
    public Mono<RawVideoListResponse> fetchTrendings(String regionCode, String pageToken, int maxResultCount) {
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

        return webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(RawVideoListResponse.class)
                .timeout(Duration.ofSeconds(youtubeDataApiProperties.getTimeout()))
                .retryWhen(Retry.backoff(youtubeDataApiProperties.getRetryCount(), Duration.ofSeconds(1))
                        .doBeforeRetry(retrySignal -> {
                            Throwable failure = retrySignal.failure();
                            long attempt = retrySignal.totalRetries() + 1;
                            log.warn("[WebClient] Retry #{} for fetching trendings (region={}, cause={})", attempt, regionCode, failure.toString());
                        }));
    }

    @Override
    public Mono<RawRegionListResponse> fetchRegions() {
        throw new UnsupportedOperationException();
    }
}
