package io.github.hamsteak.trendlapse.region.web;

import io.github.hamsteak.trendlapse.region.application.dto.RegionView;
import io.github.hamsteak.trendlapse.region.application.service.GetRegionViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
public class RegionController {
    private final GetRegionViewService getRegionViewService;

    @GetMapping
    public ResponseEntity<?> getRegions() {
        List<RegionView> regionViews = getRegionViewService.getRegionViews();

        return ResponseEntity.ok(regionViews);
    }
}
