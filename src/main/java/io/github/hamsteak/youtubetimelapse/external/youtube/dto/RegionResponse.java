package io.github.hamsteak.youtubetimelapse.external.youtube.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegionResponse {
    private final String id;
    private final Snippet snippet;

    @Getter
    @RequiredArgsConstructor
    public static class Snippet {
        private final String gl;
        private final String name;
    }
}
