package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.trending.infrastructure.TrendingRepository;
import io.github.hamsteak.youtubetimelapse.video.domain.Video;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TrendingCreator {
    private final TrendingRepository trendingRepository;
    private final VideoReader videoReader;

    @Transactional
    public void create(LocalDateTime dateTime, long videoId, int rank) {
        Video video = videoReader.read(videoId);

        trendingRepository.save(Trending.builder()
                .dateTime(dateTime)
                .video(video)
                .rank(rank)
                .build());
    }
}
