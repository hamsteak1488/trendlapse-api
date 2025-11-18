package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.region.application.component.RegionReader;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingCreateDto;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
import io.github.hamsteak.trendlapse.video.domain.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaTrendingCreator implements TrendingCreator {
    private final TrendingRepository trendingRepository;
    private final VideoFinder videoFinder;
    private final RegionReader regionReader;

    @Transactional
    @Override
    public int create(List<TrendingCreateDto> trendingCreateDtos) {
        int createdCount = 0;

        Map<String, Region> regionMap = getRegionMap(trendingCreateDtos);
        Map<String, Video> videoMap = getVideoMap(trendingCreateDtos);

        for (TrendingCreateDto dto : trendingCreateDtos) {
            Region region = regionMap.get(dto.getRegionCode());
            Video video = videoMap.get(dto.getVideoYoutubeId());

            trendingRepository.save(Trending.builder()
                    .dateTime(dto.getDateTime())
                    .video(video)
                    .rankValue(dto.getRank())
                    .region(region)
                    .build());

            createdCount++;
        }

        return createdCount;
    }

    private Map<String, Video> getVideoMap(List<TrendingCreateDto> dtos) {
        Map<String, Video> videoMap = new HashMap<>();
        List<String> videoYoutubeIds = dtos.stream()
                .map(TrendingCreateDto::getVideoYoutubeId)
                .toList();
        videoFinder.findByYoutubeIds(videoYoutubeIds)
                .forEach(video -> videoMap.put(video.getYoutubeId(), video));

        return videoMap;
    }

    private Map<String, Region> getRegionMap(List<TrendingCreateDto> dtos) {
        Map<String, Region> regionMap = new HashMap<>();
        List<String> regionCodes = dtos.stream()
                .map(TrendingCreateDto::getRegionCode)
                .distinct()
                .toList();
        regionReader.readByRegionCodes(regionCodes)
                .forEach(region -> regionMap.put(region.getRegionCode(), region));

        return regionMap;
    }
}
