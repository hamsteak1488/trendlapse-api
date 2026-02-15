package io.github.hamsteak.trendlapse.report.snapshot.infrastructure;

import io.github.hamsteak.trendlapse.report.snapshot.application.AiSnapshotReporter;
import io.github.hamsteak.trendlapse.report.snapshot.infrastructure.dto.CodexApiRequest;
import io.github.hamsteak.trendlapse.report.snapshot.infrastructure.dto.CodexApiResponse;
import io.github.hamsteak.trendlapse.youtube.domain.YoutubeNullResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Primary
@Component
@RequiredArgsConstructor
public class CodexApiSnapshotReporter implements AiSnapshotReporter {
    private final RestTemplate restTemplate;
    private final CodexApiProperties codexApiProperties;

    private static final String TRENDING_VIDEO_RANKING_SNAPSHOT_REPORT_SYSTEM_PROMPT =
            """
                    You are a professional analyst generating video ranking trend reports.
                    
                    PRIMARY OBJECTIVE:
                    Analyze and describe VIDEO RANKING CHANGES and trends over time.
                    
                    RANK HISTORY INTERPRETATION:
                    - rankHistory is ordered from oldest to newest.
                    - Each value represents the rank at a 1-hour interval.
                    - The last value is the most recent rank.
                    - Lower rank numbers indicate higher positions.
                    - If rank number decreases, the video is rising.
                    - If rank number increases, the video is falling.
                    - If rank number remains similar, the video is stable.
                    
                    PRIORITY RULES:
                    - Primary focus: video ranking movement, trends, and stability.
                    - Secondary reference: channel and region metadata may be used ONLY if directly relevant to explaining ranking behavior.
                    - Do NOT describe channel or region metadata independently.
                    - Do NOT list channel names or explain region context unless necessary for ranking interpretation.
                    
                    OUTPUT REQUIREMENTS:
                    - Output plain text only.
                    - No markdown or formatting symbols.
                    - Write exactly one paragraph.
                    - Use between 3 and 5 sentences.
                    - Maximum 120 words.
                    - Focus on meaningful ranking trends, patterns, and notable changes.
                    - Avoid unnecessary explanations and metadata descriptions.
                    - Be concise, factual, and analytical.
                    """;

    @Override
    public String report(String inputData) {
        URI requestUri = UriComponentsBuilder.fromUriString(codexApiProperties.getUrl())
                .path("/api/chat")
                .build().toUri();

        CodexApiRequest request = CodexApiRequest.builder()
                .system(TRENDING_VIDEO_RANKING_SNAPSHOT_REPORT_SYSTEM_PROMPT)
                .user(inputData)
                .build();

        CodexApiResponse response = restTemplate.postForObject(requestUri, request, CodexApiResponse.class);

        if (response == null) {
            throw new YoutubeNullResponseException("CodexApiResponse is null.");
        }

        return response.getAnswer();
    }
}
