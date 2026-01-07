package io.github.hamsteak.trendlapse.youtube.domain;

public class RegionNotFoundException extends YoutubeDataNotFoundException {
    public RegionNotFoundException(String message) {
        super(message);
    }
}
