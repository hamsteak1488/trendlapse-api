package io.github.hamsteak.youtubetimelapse.region.infrastructure;

import io.github.hamsteak.youtubetimelapse.region.domain.Region;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends Repository<Region, Long> {
    Region save(Region region);

    Optional<Region> findById(long id);

    Optional<Region> findByRegionCode(String regionCode);

    List<Region> findAll();
}
