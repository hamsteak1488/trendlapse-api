package io.github.hamsteak.trendlapse.trending.controller;

import io.github.hamsteak.trendlapse.trending.controller.dto.GetTrendingRequest;
import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trending.service.TrendingService;
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
    private final TrendingService trendingService;

    @GetMapping
    public ResponseEntity<?> getTrendings(GetTrendingRequest request) {
        return ResponseEntity.ok(
                trendingService.searchTrending(
                        TrendingSearchFilter.builder()
                                .startDateTime(request.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .endDateTime(request.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .build()
                )
        );
    }
}
