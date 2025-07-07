package io.github.hamsteak.youtubetimelapse.trending.controller;

import io.github.hamsteak.youtubetimelapse.trending.controller.dto.GetTrendingRequest;
import io.github.hamsteak.youtubetimelapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.youtubetimelapse.trending.service.TrendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                                .startDateTime(request.getStartDate())
                                .endDateTime(request.getEndDate())
                                .build()
                )
        );
    }
}
