package io.github.hamsteak.trendlapse.trending.controller;

import io.github.hamsteak.trendlapse.trending.controller.dto.GetTrendingRequest;
import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trending.service.TrendingService;
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
    private final TrendingService trendingService;

    @GetMapping
    public ResponseEntity<?> getTrendings(@Valid GetTrendingRequest request) {
        return ResponseEntity.ok(
                trendingService.searchTrending(
                        TrendingSearchFilter.builder()
                                .regionCode(request.getRegionCode())
                                .startDateTime(request.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .endDateTime(request.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .build()
                )
        );
    }

    /**
     * Batch Size
     */
    @GetMapping("/batch-size")
    public ResponseEntity<?> getTrendingsBatchSize(@Valid GetTrendingRequest request) {
        return ResponseEntity.ok(
                trendingService.searchTrendingBatchSize(
                        TrendingSearchFilter.builder()
                                .regionCode(request.getRegionCode())
                                .startDateTime(request.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .endDateTime(request.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .build()
                )
        );
    }

    /**
     * Fetch Join (Persistence Context)
     */
    @GetMapping("/fetch-join")
    public ResponseEntity<?> getTrendingsFetchJoin(@Valid GetTrendingRequest request) {
        return ResponseEntity.ok(
                trendingService.searchTrendingFetchJoin(
                        TrendingSearchFilter.builder()
                                .regionCode(request.getRegionCode())
                                .startDateTime(request.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .endDateTime(request.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .build()
                )
        );
    }

    /**
     * Join + DTO
     */
    @GetMapping("/join-dto")
    public ResponseEntity<?> getTrendingJoinDTO(@Valid GetTrendingRequest request) {
        return ResponseEntity.ok(
                trendingService.searchTrendingJoinDTO(
                        TrendingSearchFilter.builder()
                                .regionCode(request.getRegionCode())
                                .startDateTime(request.getStartDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .endDateTime(request.getEndDateTime().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                                .build()
                )
        );
    }
}
