package io.github.hamsteak.trendlapse.trending.infrastructure;

import io.github.hamsteak.trendlapse.trending.domain.Trending;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrendingRepository extends Repository<Trending, Long> {
    Trending save(Trending trending);

    Optional<Trending> findById(long id);

    List<Trending> findByDateTime(LocalDateTime dateTime);

    @Query("select t from Trending t where t.region.regionCode = :regionCode and t.dateTime between :startDateTime and :endDateTime")
    List<Trending> findByRegionAndDateTime(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
