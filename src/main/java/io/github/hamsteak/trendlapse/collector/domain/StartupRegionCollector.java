package io.github.hamsteak.trendlapse.collector.domain;

import io.github.hamsteak.trendlapse.region.domain.RegionFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRegionCollector implements ApplicationRunner {
    private final RegionFetcher regionFetcher;

    @Override
    public void run(ApplicationArguments args) {
        regionFetcher.fetch();
        log.info("Fetching region list has been completed.");
    }
}