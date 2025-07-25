package io.github.hamsteak.trendlapse.region.controller;

import io.github.hamsteak.trendlapse.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
public class RegionController {
    private final RegionService regionService;

    @PostMapping("/fetch")
    public ResponseEntity<?> fetch() {
        regionService.fetchRegions();

        return ResponseEntity.ok().build();
    }
}
