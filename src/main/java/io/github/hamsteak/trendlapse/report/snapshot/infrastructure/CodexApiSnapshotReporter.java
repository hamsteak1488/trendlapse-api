package io.github.hamsteak.trendlapse.report.snapshot.infrastructure;

import io.github.hamsteak.trendlapse.ai.domain.PromptNotFoundException;
import io.github.hamsteak.trendlapse.ai.domain.PromptRepository;
import io.github.hamsteak.trendlapse.ai.infrastructure.CodexApiProperties;
import io.github.hamsteak.trendlapse.ai.infrastructure.dto.CodexApiRequest;
import io.github.hamsteak.trendlapse.ai.infrastructure.dto.CodexApiResponse;
import io.github.hamsteak.trendlapse.report.snapshot.application.AiSnapshotReporter;
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
    private final PromptRepository promptRepository;

    private static final String AGENTS_INSTRUCTIONS_PROMPT_KEY = "trending-video-ranking-snapshot-agents-instructions";
    private static final String SYSTEM_PROMPT_KEY = "trending-video-ranking-snapshot-system";

    @Override
    public String report(String inputData) {
        URI requestUri = UriComponentsBuilder.fromUriString(codexApiProperties.getUrl())
                .path("/api/chat")
                .build().toUri();

        CodexApiRequest request = CodexApiRequest.builder()
                .agentInstructions(getAgentInstructions())
                .system(getSystemPrompt())
                .user(inputData)
                .build();

        CodexApiResponse response = restTemplate.postForObject(requestUri, request, CodexApiResponse.class);

        if (response == null) {
            throw new IllegalStateException("CodexApiResponse is null.");
        }

        return response.getAnswer();
    }

    private String getAgentInstructions() {
        return promptRepository.findById(AGENTS_INSTRUCTIONS_PROMPT_KEY)
                .orElseThrow(() -> new PromptNotFoundException("Cannot find prompt. (promptId=" + AGENTS_INSTRUCTIONS_PROMPT_KEY + ")"))
                .getContent();
    }

    private String getSystemPrompt() {
        return promptRepository.findById(SYSTEM_PROMPT_KEY)
                .orElseThrow(() -> new PromptNotFoundException("Cannot find prompt. (promptId=" + SYSTEM_PROMPT_KEY + ")"))
                .getContent();
    }
}
