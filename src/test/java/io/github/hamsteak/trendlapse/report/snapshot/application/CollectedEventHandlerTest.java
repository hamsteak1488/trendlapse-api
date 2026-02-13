package io.github.hamsteak.trendlapse.report.snapshot.application;

import io.github.hamsteak.trendlapse.collector.application.CollectedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CollectedEventHandlerTest {
    @Autowired
    ApplicationEventPublisher publisher;
    @MockitoSpyBean
    CollectedEventHandler handler;
    @MockitoBean
    CreateTrendingVideoRankingSnapshotReportService createTrendingVideoRankingSnapshotReportService;
    @Autowired
    TransactionTemplate tx;

    @Test
    void publish_CollectedEvent_invokes_handle() {
        // given
        CollectedEvent event = new CollectedEvent(List.of(1L, 2L, 3L));

        // when
        tx.executeWithoutResult(status -> {
            publisher.publishEvent(event);
        });

        // then
        await().atMost(3, TimeUnit.of(ChronoUnit.SECONDS))
                .untilAsserted(() -> verify(handler).handle(event));
    }
}