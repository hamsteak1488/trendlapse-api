package io.github.hamsteak.trendlapse.region.application.component;

import io.github.hamsteak.trendlapse.region.application.dto.RegionCreateDto;
import io.github.hamsteak.trendlapse.region.domain.Region;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionCreator {
    private final RegionRepository regionRepository;

    public int create(List<RegionCreateDto> dtos) {
        for (RegionCreateDto dto : dtos) {
            regionRepository.save(Region.builder()
                    .regionCode(dto.getRegionCode())
                    .name(dto.getName())
                    .isoCode(dto.getIsoCode())
                    .build());
        }

        return dtos.size();
    }
}
