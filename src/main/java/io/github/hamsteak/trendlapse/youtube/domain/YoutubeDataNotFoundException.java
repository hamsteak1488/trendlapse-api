package io.github.hamsteak.trendlapse.youtube.domain;

public class YoutubeDataNotFoundException extends RuntimeException {
    public YoutubeDataNotFoundException(String message) {
        super(message);
    }
}
