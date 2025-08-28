package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.domain.RegionReader;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final RegionReader regionReader;
    private final TrendingCollector trendingCollector;
    private final CollectSchedulerProperties collectSchedulerProperties;

    @Value("${only-korea-region:false}")
    private boolean onlyKoreaRegion;

    @Timed("collect.whole")
    //    @Scheduled(cron = "${collect-scheduler.collect-cron}", zone = "UTC")
    public void collect() {
        if (!regionReader.isReady()) {
            log.info("RegionReader is not ready yet. Skipping scheduled task.");
            return;
        }

        LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        List<String> regionCodes = regionReader.readAll().stream().map(Region::getRegionCode).toList();

        if (regionCodes.size() < 110) {
            log.warn("Region list contains fewer than 110 items. This may cause incomplete processing.");
        }

        if (onlyKoreaRegion) {
            regionCodes = regionCodes.stream().filter(regionCode -> regionCode.equals("KR")).toList();
        }

        trendingCollector.collect(dateTime, collectSchedulerProperties.getCollectSize(), regionCodes);
    }
}
