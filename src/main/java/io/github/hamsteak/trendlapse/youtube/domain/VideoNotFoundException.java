package io.github.hamsteak.trendlapse.youtube.domain;

public class VideoNotFoundException extends YoutubeDataNotFoundException {
    public VideoNotFoundException(String message) {
        super(message);
    }
}
