package io.github.hamsteak.trendlapse.youtube.domain;

public class YoutubeNullResponseException extends RuntimeException {
    public YoutubeNullResponseException(String message) {
        super(message);
    }
}
