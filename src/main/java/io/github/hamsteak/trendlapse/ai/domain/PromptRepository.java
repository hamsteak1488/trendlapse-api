package io.github.hamsteak.trendlapse.ai.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository
        extends JpaRepository<Prompt, String> {

}
