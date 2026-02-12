package io.github.hamsteak.trendlapse.report.snapshot.infrastructure;

import io.github.hamsteak.trendlapse.report.snapshot.application.AiSnapshotReporter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAiSnapshotReporter implements AiSnapshotReporter {
    private final ChatClient chatClient;

    @Override
    public String report(String message) {
        ChatResponse chatResponse = chatClient.prompt(message)
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
}
