package io.github.hamsteak.trendlapse.youtube.domain;

public class ChannelNotFoundException extends YoutubeDataNotFoundException {
    public ChannelNotFoundException(String message) {
        super(message);
    }
}
