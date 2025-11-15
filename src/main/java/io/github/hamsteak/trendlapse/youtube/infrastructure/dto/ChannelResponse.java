package io.github.hamsteak.trendlapse.youtube.infrastructure.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChannelResponse {
    private final String id;
    private final Snippet snippet;

    @Getter
    @RequiredArgsConstructor
    public static class Snippet {
        private final String title;
        private final Thumbnails thumbnails;

        @Getter
        @RequiredArgsConstructor
        public static class Thumbnails {
            private final Thumbnail high;

            @Getter
            @RequiredArgsConstructor
            public static class Thumbnail {
                private final String url;
            }
        }
    }
}
