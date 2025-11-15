package io.github.hamsteak.trendlapse.region.application.component;

import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionPutter {
    private final RegionRepository regionRepository;

    public void put(String regionCode, String name, String isoCode) {
        Region region = regionRepository.findByRegionCode(regionCode)
                .orElse(null);

        if (region != null) {
            return;
        }

        regionRepository.save(Region.builder()
                .regionCode(regionCode)
                .name(name)
                .isoCode(isoCode)
                .build());
    }
}
