package io.github.hamsteak.trendlapse.trendingsnapshot.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    /*
        H2 does not support ORDER BY in DELETE statements,
        so the query is designed to work with both MySQL and H2 though it's a bit more complex.
    */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = """
            delete from trending_snapshot
            where id in (
                select id from (
                    select id
                    from trending_snapshot
                    where captured_at < :dateTime
                    order by captured_at
                    limit :count
                ) t
            )
            """, nativeQuery = true)
    int deleteByDateTimeBefore(LocalDateTime dateTime, int count);
}
