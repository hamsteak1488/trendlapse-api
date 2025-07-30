package io.github.hamsteak.trendlapse.common.errors.exception;

public class VideoNotFoundException extends YoutubeDataNotFoundException {
    public VideoNotFoundException(String message) {
        super(message);
    }
}
