package io.github.hamsteak.trendlapse.region.controller;

import io.github.hamsteak.trendlapse.region.domain.RegionDetail;
import io.github.hamsteak.trendlapse.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
public class RegionController {
    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<?> getRegions() {
        List<RegionDetail> regionDetails = regionService.getRegionDetails();

        return ResponseEntity.ok(regionDetails);
    }

    @PostMapping("/fetch")
    public ResponseEntity<?> fetch() {
        regionService.fetchRegions();

        return ResponseEntity.ok().build();
    }
}
