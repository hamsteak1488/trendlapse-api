package io.github.hamsteak.trendlapse.region.application.service;

import io.github.hamsteak.trendlapse.collector.application.component.fetcher.RegionFetcher;
import io.github.hamsteak.trendlapse.region.application.dto.RegionDetail;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;
    private final RegionFetcher regionFetcher;

    public List<RegionDetail> getRegionDetails() {
        return regionRepository.findAll().stream()
                .map(region ->
                        RegionDetail.builder()
                                .regionCode(region.getRegionCode())
                                .name(region.getName())
                                .build())
                .toList();
    }

    public void fetchRegions() {
        regionFetcher.fetch();
    }
}
