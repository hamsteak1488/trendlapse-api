package io.github.hamsteak.trendlapse.trendingsnapshot.web;

import io.github.hamsteak.trendlapse.trendingsnapshot.application.SearchTrendingService;
import io.github.hamsteak.trendlapse.trendingsnapshot.application.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trendingsnapshot.web.dto.SearchTrendingRequest;
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
public class TrendingController {
    private final SearchTrendingService searchTrendingService;

    @GetMapping
    public ResponseEntity<?> searchTrendings(@Valid SearchTrendingRequest request) {
        return ResponseEntity.ok(
                searchTrendingService.search(
                        TrendingSearchFilter.builder()
                                .regionId(request.getRegionId())
                                .startDateTime(request.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .endDateTime(request.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .build()
                )
        );
    }
}
