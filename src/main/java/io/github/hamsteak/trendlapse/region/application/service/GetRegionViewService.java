package io.github.hamsteak.trendlapse.region.application.service;

import io.github.hamsteak.trendlapse.region.application.dto.RegionView;
import io.github.hamsteak.trendlapse.region.domain.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRegionViewService {
    private final RegionRepository regionRepository;

    public List<RegionView> getRegionViews() {
        return regionRepository.findAll().stream()
                .map(region ->
                        RegionView.builder()
                                .regionId(region.getId())
                                .name(region.getName())
                                .build())
                .toList();
    }
}
