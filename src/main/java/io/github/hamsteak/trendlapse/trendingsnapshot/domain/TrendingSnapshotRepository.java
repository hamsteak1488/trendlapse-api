package io.github.hamsteak.trendlapse.trendingsnapshot.domain;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendingSnapshotRepository extends JpaRepository<TrendingSnapshot, Long> {
    List<TrendingSnapshot> findByRegionIdAndCapturedAtBetween(String regionId, LocalDateTime startInclusive, LocalDateTime endInclusive);

    @Query("""
            select distinct t.capturedAt
            from TrendingSnapshot t
            where t.regionId = :regionId and t.capturedAt between :startDateTime and :endDateTime
            """)
    List<LocalDateTime> findCaptureTimesByCapturedAtBetween(String regionId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<TrendingSnapshot> findByCapturedAtBefore(LocalDateTime dateTime, Limit limit);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from TrendingSnapshot t where t.id in :ids")
    int deleteByIdIn(List<Long> ids);
}
