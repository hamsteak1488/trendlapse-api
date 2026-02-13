package io.github.hamsteak.trendlapse.report.snapshot.application;

import io.github.hamsteak.trendlapse.collector.application.CollectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CollectedEventHandler {
    private final CreateTrendingVideoRankingSnapshotReportService createTrendingVideoRankingSnapshotReportService;

    @Async
    @TransactionalEventListener
    public void handle(CollectedEvent event) {
        if (true) return;

        List<Long> snapshotIds = event.getCollectedSnapshotIds();
        String snapshotIdsString = String.join(",", snapshotIds.stream().map(Object::toString).toList());
        log.info("Starting to create report for snapshot ids {}", snapshotIdsString);

        event.getCollectedSnapshotIds().forEach(snapshotId ->
                createTrendingVideoRankingSnapshotReportService.create(snapshotId)
        );

        log.info("Completed to create report for snapshot ids {}", snapshotIdsString);
    }
}
