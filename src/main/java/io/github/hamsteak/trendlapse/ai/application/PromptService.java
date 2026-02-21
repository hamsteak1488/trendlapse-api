package io.github.hamsteak.trendlapse.ai.application;

import io.github.hamsteak.trendlapse.ai.domain.Prompt;
import io.github.hamsteak.trendlapse.ai.domain.PromptNotFoundException;
import io.github.hamsteak.trendlapse.ai.domain.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptRepository promptRepository;

    public Prompt getPrompt(String promptId) {
        return promptRepository.findById(promptId)
                .orElseThrow(() -> new PromptNotFoundException("Cannot find prompt. (promptId=" + promptId + ")"));
    }

    @Transactional
    public void putPrompt(String promptId, String content) {
        Optional<Prompt> promptOptional = promptRepository.findById(promptId);

        if (promptOptional.isPresent()) {
            Prompt prompt = promptOptional.get();
            prompt.setContent(content);
        } else {
            promptRepository.save(new Prompt(promptId, content));
        }
    }
}
