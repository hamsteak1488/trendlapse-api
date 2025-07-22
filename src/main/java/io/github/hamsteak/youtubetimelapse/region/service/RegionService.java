package io.github.hamsteak.youtubetimelapse.region.service;

import io.github.hamsteak.youtubetimelapse.region.domain.RegionFetcher;
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
