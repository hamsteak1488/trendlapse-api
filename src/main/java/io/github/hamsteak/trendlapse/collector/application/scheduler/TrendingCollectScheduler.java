package io.github.hamsteak.trendlapse.collector.application.scheduler;

import io.github.hamsteak.trendlapse.collector.application.CollectSchedulerProperties;
import io.github.hamsteak.trendlapse.collector.application.component.collector.TrendingCollector;
import io.github.hamsteak.trendlapse.collector.application.component.fetcher.RegionFetcher;
import io.github.hamsteak.trendlapse.collector.application.component.storer.RegionStorer;
import io.github.hamsteak.trendlapse.collector.application.dto.RegionItem;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendingCollectScheduler {
    private final RegionRepository regionRepository;
    private final RegionFetcher regionFetcher;
    private final RegionStorer regionStorer;
    private final TrendingCollector trendingCollector;
    private final CollectSchedulerProperties collectSchedulerProperties;

    @Timed("collect.whole")
    @Scheduled(cron = "${collect-scheduler.collect-cron}", zone = "UTC")
    public void collect() {
        List<String> regionCodes = regionRepository.findAll().stream()
                .map(Region::getRegionCode)
                .toList();

        if (regionCodes.size() < regionFetcher.getExternalRegionCount()) {
            log.info("Starting to fetch regions because the number of regions is fewer than {} items.", regionFetcher.getExternalRegionCount());
            List<RegionItem> regionItems = regionFetcher.fetch();
            regionStorer.store(regionItems);
            regionCodes = regionRepository.findAll().stream()
                    .map(Region::getRegionCode)
                    .toList();
        }

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        trendingCollector.collect(now, collectSchedulerProperties.getCollectSize(), regionCodes);
    }
}
