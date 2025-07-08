package io.github.hamsteak.youtubetimelapse.trending.infrastructure;

import io.github.hamsteak.youtubetimelapse.trending.domain.Trending;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingRepository extends Repository<Trending, Long> {
    Trending save(Trending trending);
    List<Trending> findByDateTime(LocalDateTime dateTime);
    List<Trending> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
