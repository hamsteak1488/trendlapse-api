package io.github.hamsteak.trendlapse.youtube.domain;

public class TrendingNotFoundException extends YoutubeDataNotFoundException {
    public TrendingNotFoundException(String message) {
        super(message);
    }
}
