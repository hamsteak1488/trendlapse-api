package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.region.application.component.RegionReader;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingCreateDto;
import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
import io.github.hamsteak.trendlapse.video.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class JdbcTrendingCreator implements TrendingCreator {
    private final VideoFinder videoFinder;
    private final RegionReader regionReader;

    private final JdbcTemplate jdbcTemplate;
    private static final int INSERT_BATCH_SIZE = 1000;

    @Transactional
    @Override
    public int create(List<TrendingCreateDto> trendingCreateDtos) {
        Map<String, Region> regionMap = getRegionMap(trendingCreateDtos);
        Map<String, Video> videoMap = getVideoMap(trendingCreateDtos);

        jdbcTemplate.batchUpdate(
                "INSERT INTO trending(date_time, video_id, rank_value, region_id) VALUES (?, ?, ?, ?)",
                trendingCreateDtos,
                INSERT_BATCH_SIZE,
                (ps, dto) -> {
                    Video video = videoMap.get(dto.getVideoYoutubeId());
                    Region region = regionMap.get(dto.getRegionCode());

                    ps.setObject(1, dto.getDateTime());
                    ps.setLong(2, video.getId());
                    ps.setInt(3, dto.getRank());
                    ps.setLong(4, region.getId());
                }
        );

        return trendingCreateDtos.size();
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
