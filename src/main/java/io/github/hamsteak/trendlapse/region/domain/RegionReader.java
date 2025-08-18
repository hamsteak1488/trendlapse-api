package io.github.hamsteak.trendlapse.region.domain;

import io.github.hamsteak.trendlapse.common.errors.exception.RegionNotFoundException;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionReader {
    private final RegionRepository regionRepository;

    @Getter
    @Setter
    private boolean isReady = false;

    @Transactional(readOnly = true)
    public Region read(long regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new RegionNotFoundException("Cannot find region (id:" + regionId + ")"));
    }

    @Transactional(readOnly = true)
    public Region readByRegionCode(String regionCode) {
        return regionRepository.findByRegionCode(regionCode)
                .orElseThrow(() -> new RegionNotFoundException("Cannot find region (regionCode:" + regionCode + ")"));
    }

    @Transactional(readOnly = true)
    public List<Region> read(List<Long> regionIds) {
        return regionRepository.findByIdIn(regionIds);
    }

    @Transactional(readOnly = true)
    public List<Region> readAll() {
        return regionRepository.findAll();
    }
}
