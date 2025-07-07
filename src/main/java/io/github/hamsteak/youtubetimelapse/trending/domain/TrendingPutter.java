package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.trending.infrastructure.TrendingRepository;
import io.github.hamsteak.youtubetimelapse.video.domain.Video;
import io.github.hamsteak.youtubetimelapse.video.domain.VideoPutter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TrendingPutter {
    private final TrendingRepository trendingRepository;
    private final VideoPutter videoPutter;

    @Transactional
    public Trending put(LocalDateTime dateTime, String videoYoutubeId, int rank) {
        Video trendingVideo = videoPutter.put(videoYoutubeId);

        return trendingRepository.save(Trending.builder()
                .dateTime(dateTime)
                .video(trendingVideo)
                .rank(rank)
                .build()
        );
    }
}
