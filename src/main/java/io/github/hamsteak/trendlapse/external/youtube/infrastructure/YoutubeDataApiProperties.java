package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youtube-data-api")
@Getter
@RequiredArgsConstructor
public class YoutubeDataApiProperties {
    private final String baseUrl;
    private final String apiKey;
    private final int maxFetchCount;
}
