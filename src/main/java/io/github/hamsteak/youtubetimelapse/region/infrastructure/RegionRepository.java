package io.github.hamsteak.youtubetimelapse.region.infrastructure;

import io.github.hamsteak.youtubetimelapse.region.domain.Region;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface RegionRepository extends Repository<Region, Long> {
    Region save(Region region);
    Region findById(long id);
    List<Region> findAll();
}
