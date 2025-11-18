package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.collector.application.dto.RegionItem;
import io.github.hamsteak.trendlapse.region.application.component.RegionCreator;
import io.github.hamsteak.trendlapse.region.application.dto.RegionCreateDto;
import io.github.hamsteak.trendlapse.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionStorer {
    private final RegionRepository regionRepository;
    private final RegionCreator regionCreator;

    public int store(List<RegionItem> regionItems) {
        List<String> existingRegionCodes = regionRepository.findRegionCodeByRegionCodeIn(
                regionItems.stream()
                        .map(RegionItem::getRegionCode)
                        .toList());

        List<RegionCreateDto> dtosToInsert = regionItems.stream()
                .filter(item -> !existingRegionCodes.contains(item.getRegionCode()))
                .map(item -> new RegionCreateDto(item.getRegionCode(), item.getName(), item.getIsoCode()))
                .toList();

        regionCreator.create(dtosToInsert);

        return dtosToInsert.size();
    }
}
