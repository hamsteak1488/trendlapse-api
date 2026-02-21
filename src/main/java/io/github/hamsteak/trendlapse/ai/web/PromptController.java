package io.github.hamsteak.trendlapse.ai.web;

import io.github.hamsteak.trendlapse.ai.application.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prompts")
@RequiredArgsConstructor
public class PromptController {
    private final PromptService promptService;

    @GetMapping("/{promptId}")
    public ResponseEntity<?> getPrompt(@PathVariable String promptId) {
        return ResponseEntity.ok(promptService.getPrompt(promptId));
    }

    @PutMapping("/{promptId}")
    public ResponseEntity<?> putPrompt(@PathVariable String promptId, @RequestBody String content) {
        promptService.putPrompt(promptId, content);
        return ResponseEntity.ok().build();
    }
}
