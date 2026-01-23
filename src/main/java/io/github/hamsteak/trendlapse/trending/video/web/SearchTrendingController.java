package io.github.hamsteak.trendlapse.trending.video.web;

import io.github.hamsteak.trendlapse.trending.video.application.SearchTrendingVideoRankingSnapshotService;
import io.github.hamsteak.trendlapse.trending.video.application.dto.TrendingVideoRankingSnapshotSearchFilter;
import io.github.hamsteak.trendlapse.trending.video.web.dto.SearchTrendingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;

@RestController
@RequestMapping("/trendings")
@RequiredArgsConstructor
public class SearchTrendingController {
    private final SearchTrendingVideoRankingSnapshotService searchTrendingVideoRankingSnapshotService;

    @GetMapping
    public ResponseEntity<?> searchTrendings(@Valid SearchTrendingRequest request) {
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
}
