package io.github.hamsteak.youtubetimelapse.region.domain;

import io.github.hamsteak.youtubetimelapse.common.errors.errorcode.CommonErrorCode;
import io.github.hamsteak.youtubetimelapse.common.errors.exception.RestApiException;
import io.github.hamsteak.youtubetimelapse.region.infrastructure.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegionReader {
    private final RegionRepository regionRepository;

    @Transactional(readOnly = true)
    public Region read(long regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND, "Cannot find region (id:" + regionId + ")"));
    }

    @Transactional(readOnly = true)
    public List<Region> readAll() {
        return regionRepository.findAll();
    }
}
