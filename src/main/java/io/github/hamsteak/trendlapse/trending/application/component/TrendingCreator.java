package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.application.component.RegionReader;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import io.github.hamsteak.trendlapse.video.domain.Video;
import io.github.hamsteak.trendlapse.video.application.component.VideoReader;
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
    public void create(LocalDateTime dateTime, String videoYoutubeId, int rank, String regionCode) {
        Video video = videoReader.readByYoutubeId(videoYoutubeId);
        Region region = regionReader.readByRegionCode(regionCode);

        trendingRepository.save(Trending.builder()
                .dateTime(dateTime)
                .video(video)
                .rankValue(rank)
                .region(region)
                .build());
    }
}
