package io.github.hamsteak.trendlapse.external.youtube.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "youtube-data-api")
@Validated
@Getter
@RequiredArgsConstructor
public class YoutubeDataApiProperties {
    @URL
    private final String baseUrl;

    private final String apiKey;

    @Range(min = 1, max = 50)
    private final int maxFetchCount;

    private final boolean useLog;

    @Range(min = 0, max = 5)
    private final int maxRetryCount;
}
