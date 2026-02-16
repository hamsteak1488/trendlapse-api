package io.github.hamsteak.trendlapse.report.snapshot.infrastructure;

import io.github.hamsteak.trendlapse.report.snapshot.application.AiSnapshotReporter;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReportSystemPrompt;
import io.github.hamsteak.trendlapse.report.snapshot.domain.TrendingVideoRankingSnapshotReportSystemPromptRepository;
import io.github.hamsteak.trendlapse.report.snapshot.infrastructure.dto.CodexApiRequest;
import io.github.hamsteak.trendlapse.report.snapshot.infrastructure.dto.CodexApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class CodexApiSnapshotReporter implements AiSnapshotReporter {
    private final RestTemplate restTemplate;
    private final CodexApiProperties codexApiProperties;
    private final TrendingVideoRankingSnapshotReportSystemPromptRepository snapshotReportSystemPromptRepository;

    @Override
    public String report(String inputData) {
        URI requestUri = UriComponentsBuilder.fromUriString(codexApiProperties.getUrl())
                .path("/api/chat")
                .build().toUri();

        CodexApiRequest request = CodexApiRequest.builder()
                .system(getSystemPrompt())
                .user(inputData)
                .build();

        CodexApiResponse response = restTemplate.postForObject(requestUri, request, CodexApiResponse.class);

        if (response == null) {
            throw new IllegalStateException("CodexApiResponse is null.");
        }

        return response.getAnswer();
    }

    private String getSystemPrompt() {
        List<TrendingVideoRankingSnapshotReportSystemPrompt> systemPrompts = snapshotReportSystemPromptRepository.findAll();

        if (systemPrompts.isEmpty()) {
            throw new IllegalStateException("Cannot find system prompt.");
        }
        if (systemPrompts.size() > 1) {
            throw new IllegalStateException("There must be only one system prompt.");
        }

        return systemPrompts.get(0).getContent();
    }
}
