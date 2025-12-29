package io.github.hamsteak.trendlapse.youtube.infrastructure.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawRegion {
    @Getter
    private final String id;
    private final Snippet snippet;

    @Getter
    @RequiredArgsConstructor
    public static class Snippet {
        private final String name;
    }

    public String getName() {
        return snippet.getName();
    }
}
