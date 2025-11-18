package io.github.hamsteak.trendlapse.region.application.component;

import io.github.hamsteak.trendlapse.global.errors.exception.RegionNotFoundException;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class RegionReader {
    private final RegionRepository regionRepository;

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
    public List<Region> readByRegionCodes(List<String> regionCodes) {
        List<Region> regions = regionRepository.findByRegionCodeIn(regionCodes);

        List<String> missingRegionCodes = regions.stream().map(Region::getRegionCode).filter(Predicate.not(regionCodes::contains)).toList();
        if (!missingRegionCodes.isEmpty()) {
            String joined = String.join(",", missingRegionCodes.stream().map(String::valueOf).toList());
            throw new RegionNotFoundException("Cannot find region (id:" + joined + ")");
        }

        return regions;
    }
}
