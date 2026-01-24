package io.github.hamsteak.trendlapse.youtube.infrastructure.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RawVideo {
    private final String id;
    private final Snippet snippet;
    private final Statistics statistics;

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

    @Getter
    @RequiredArgsConstructor
    public static class Statistics {
        private final long viewCount;
        private final long likeCount;
        private final long commentCount;
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

    public long getViewCount() {
        return statistics.getViewCount();
    }

    public long getLikeCount() {
        return statistics.getLikeCount();
    }

    public long getCommentCount() {
        return statistics.getCommentCount();
    }
}
