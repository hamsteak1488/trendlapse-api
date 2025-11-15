package io.github.hamsteak.trendlapse.global.errors.exception;

public class VideoNotFoundException extends YoutubeDataNotFoundException {
    public VideoNotFoundException(String message) {
        super(message);
    }
}
