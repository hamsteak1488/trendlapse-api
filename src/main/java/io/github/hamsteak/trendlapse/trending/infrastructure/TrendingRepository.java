package io.github.hamsteak.trendlapse.trending.infrastructure;

import io.github.hamsteak.trendlapse.trending.domain.Trending;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrendingRepository extends Repository<Trending, Long> {
    Trending save(Trending trending);

    Optional<Trending> findById(long id);

    List<Trending> findByDateTime(LocalDateTime dateTime);

    List<Trending> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
