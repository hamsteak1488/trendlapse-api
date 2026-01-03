package io.github.hamsteak.trendlapse.collector.web;

import io.github.hamsteak.trendlapse.collector.application.CollectTrendingSnapshotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/collector")
@Slf4j
public class CollectTrendingSnapshotController {
    private final CollectTrendingSnapshotService collectTrendingSnapshotService;
    private final String internalKey;

    public CollectTrendingSnapshotController(
            CollectTrendingSnapshotService collectTrendingSnapshotService,
            @Value("${internal-key}") String internalKey
    ) {
        this.collectTrendingSnapshotService = collectTrendingSnapshotService;
        this.internalKey = internalKey;
    }

    @PostMapping("/collect")
    public ResponseEntity<?> collect(@RequestHeader("X-Internal-Key") String requestInternalKey) {
        if (!requestInternalKey.equals(internalKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Starting scheduled trending collection job.");

        collectTrendingSnapshotService.collect(LocalDateTime.now(Clock.systemUTC()));

        log.info("Completed scheduled trending collection job.");

        return ResponseEntity.ok().build();
    }
}
