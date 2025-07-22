package io.github.hamsteak.youtubetimelapse.region.domain;

import io.github.hamsteak.youtubetimelapse.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionCreator {
    private final RegionRepository regionRepository;

    public void create(String regionCode, String name, String isoCode) {
        regionRepository.save(Region.builder()
                .regionCode(regionCode)
                .name(name)
                .isoCode(isoCode)
                .build());
    }
}
