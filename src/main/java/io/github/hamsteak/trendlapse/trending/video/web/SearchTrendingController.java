package io.github.hamsteak.trendlapse.trending.video.web;

import io.github.hamsteak.trendlapse.trending.video.application.SearchTrendingVideoRankingSnapshotService;
import io.github.hamsteak.trendlapse.trending.video.application.SearchTrendingVideoStatisticsService;
import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoRankingSnapshotSearchFilter;
import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoRankingSnapshotView;
import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoStatisticsView;
import io.github.hamsteak.trendlapse.trending.video.web.dto.SearchTrendingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/trendings/videos")
@RequiredArgsConstructor
public class SearchTrendingController {
    private final SearchTrendingVideoRankingSnapshotService searchTrendingVideoRankingSnapshotService;
    private final SearchTrendingVideoStatisticsService searchTrendingVideoStatisticsService;

    @GetMapping("/ranking-snapshots")
    public ResponseEntity<List<TrendingVideoRankingSnapshotView>> searchSnapshots(@Valid SearchTrendingRequest request) {
        return ResponseEntity.ok(
                searchTrendingVideoRankingSnapshotService.search(
                        TrendingVideoRankingSnapshotSearchFilter.builder()
                                .regionId(request.getRegionId())
                                .startDateTime(request.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .endDateTime(request.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .build()
                )
        );
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<TrendingVideoStatisticsView>> search(long videoId) {
        return ResponseEntity.ok(searchTrendingVideoStatisticsService.search(videoId));
    }
}
