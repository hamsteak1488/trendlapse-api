package io.github.hamsteak.youtubetimelapse.trending.domain;

import io.github.hamsteak.youtubetimelapse.region.domain.Region;
import io.github.hamsteak.youtubetimelapse.region.domain.RegionReader;
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
    private final RegionReader regionReader;

    @Transactional
    public void create(LocalDateTime dateTime, long videoId, int rank, long regionId) {
        Video video = videoReader.read(videoId);
        Region region = regionReader.read(regionId);

        trendingRepository.save(Trending.builder()
                .dateTime(dateTime)
                .video(video)
                .rank(rank)
                .region(region)
                .build());
    }
}
