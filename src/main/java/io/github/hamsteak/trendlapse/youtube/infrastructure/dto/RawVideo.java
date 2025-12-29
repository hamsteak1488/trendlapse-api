package io.github.hamsteak.trendlapse.youtube.infrastructure.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawVideo {
    private final String id;
    private final Snippet snippet;

    @Getter
    @RequiredArgsConstructor
    public static class Snippet {
        private final String title;
        private final String channelId;
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

    public String getYoutubeId() {
        return id;
    }

    public String getTitle() {
        return snippet.getTitle();
    }

    public String getThumbnailUrl() {
        return snippet.getThumbnails().getHigh().getUrl();
    }

    public String getChannelYoutubeId() {
        return snippet.getChannelId();
    }
}
