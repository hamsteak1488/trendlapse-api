package io.github.hamsteak.trendlapse.region.service;

import io.github.hamsteak.trendlapse.region.domain.RegionFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionFetcher regionFetcher;

    public void fetchRegions() {
        regionFetcher.fetch();
    }
}
