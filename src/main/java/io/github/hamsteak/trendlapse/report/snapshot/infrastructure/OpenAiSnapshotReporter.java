package io.github.hamsteak.trendlapse.report.snapshot.infrastructure;

import io.github.hamsteak.trendlapse.ai.domain.Prompt;
import io.github.hamsteak.trendlapse.ai.domain.PromptRepository;
import io.github.hamsteak.trendlapse.report.snapshot.application.AiSnapshotReporter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenAiSnapshotReporter implements AiSnapshotReporter {
    private final ChatClient chatClient;
    private final PromptRepository snapshotReportSystemPromptRepository;

    @Override
    public String report(String inputData) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .system(getSystemPrompt())
                .user(inputData)
                .call()
                .chatResponse();

        if (chatResponse == null) {
            throw new IllegalStateException("ChatRespons is null.");
        }

        return chatResponse
                .getResult()
                .getOutput()
                .getText();
    }

    private String getSystemPrompt() {
        List<Prompt> systemPrompts = snapshotReportSystemPromptRepository.findAll();

        if (systemPrompts.isEmpty()) {
            throw new IllegalStateException("Cannot find system prompt.");
        }
        if (systemPrompts.size() > 1) {
            throw new IllegalStateException("There must be only one system prompt.");
        }

        return systemPrompts.get(0).getContent();
    }
}
