package io.github.hamsteak.trendlapse.youtube.infrastructure;

import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.ChannelListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RegionListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.TrendingListResponse;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.VideoListResponse;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Timed("youtube.api.call")
@Component
@RequiredArgsConstructor
public class WebClientYoutubeDataApiCaller implements NonblockingYoutubeDataApiCaller {
    private final YoutubeDataApiProperties youtubeDataApiProperties;
    private final WebClient webClient;

    @Override
    public Mono<ChannelListResponse> fetchChannels(List<String> channelYoutubeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<VideoListResponse> fetchVideos(List<String> videoYoutubeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mono<TrendingListResponse> fetchTrendings(int maxResultCount, String regionCode, String pageToken) {
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

        return webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(TrendingListResponse.class);
    }

    @Override
    public Mono<RegionListResponse> fetchRegions() {
        throw new UnsupportedOperationException();
    }
}
