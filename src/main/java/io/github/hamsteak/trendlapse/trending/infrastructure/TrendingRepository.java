package io.github.hamsteak.trendlapse.trending.infrastructure;

import io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrendingRepository extends Repository<Trending, Long> {
    Trending save(Trending trending);

    Optional<Trending> findById(long id);

    List<Trending> findByDateTime(LocalDateTime dateTime);

    @Query("""
            select distinct t.dateTime
            from Trending t
            where t.region.regionCode = :regionCode and t.dateTime between :startDateTime and :endDateTime
            """)
    List<LocalDateTime> findDateTimes(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("""
            select t
            from Trending t
            where t.region.regionCode = :regionCode and t.dateTime between :startDateTime and :endDateTime
            """)
    List<Trending> findByRegionAndDateTimeBetween(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("""
            select t
            from Trending t
                join fetch t.video
                join fetch t.video.channel
            where t.region.regionCode = :regionCode and t.dateTime between :startDateTime and :endDateTime
            """)
    List<Trending> findWithByRegionAndDateTimeBetweenFetchJoin(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("""
            select new io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail(
                t.dateTime,
                t.rankValue,
                new io.github.hamsteak.trendlapse.video.application.dto.VideoDetail(
                    v.id,
                    c.id,
                    v.youtubeId,
                    v.title,
                    v.thumbnailUrl
                ),
                new io.github.hamsteak.trendlapse.channel.application.dto.ChannelDetail(
                    c.id,
                    c.youtubeId,
                    c.title,
                    c.thumbnailUrl
                )
            )
            from Trending t
                join Video v on t.video.id = v.id
                join Channel c on v.channel.id = c.id
            where t.region.regionCode = :regionCode and t.dateTime between :startDateTime and :endDateTime
            """)
    List<TrendingDetail> findDetailByRegionAndDateTimeBetween(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("""
            select new io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail(
                t.dateTime,
                t.rankValue,
                new io.github.hamsteak.trendlapse.video.application.dto.VideoDetail(
                    v.id,
                    c.id,
                    v.youtubeId,
                    v.title,
                    v.thumbnailUrl
                ),
                new io.github.hamsteak.trendlapse.channel.application.dto.ChannelDetail(
                    c.id,
                    c.youtubeId,
                    c.title,
                    c.thumbnailUrl
                )
            )
            from Trending t
                join Video v on t.video.id = v.id
                join Channel c on v.channel.id = c.id
            where t.region.regionCode = :regionCode and t.dateTime in :dateTimes
            """)
    List<TrendingDetail> findDetailByRegionAndDateTimeIn(String regionCode, List<LocalDateTime> dateTimes);

    @Query("""
            select new io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail(
                t.dateTime,
                t.rankValue,
                new io.github.hamsteak.trendlapse.video.application.dto.VideoDetail(
                    v.id,
                    c.id,
                    v.youtubeId,
                    v.title,
                    v.thumbnailUrl
                ),
                new io.github.hamsteak.trendlapse.channel.application.dto.ChannelDetail(
                    c.id,
                    c.youtubeId,
                    c.title,
                    c.thumbnailUrl
                )
            )
            from Trending t
                join Video v on t.video.id = v.id
                join Channel c on v.channel.id = c.id
            where t.region.regionCode = :regionCode and t.dateTime = :dateTime
            """)
    List<TrendingDetail> findDetailByRegionAndDateTime(String regionCode, LocalDateTime dateTime);

    /*
        H2 does not support ORDER BY in DELETE statements,
        so the query is designed to work with both MySQL and H2 though it's a bit more complex.
    */
    @Modifying
    @Transactional
    @Query(value = """
            delete from trending
            where id in (
                select id from (
                    select id
                    from trending
                    where date_time < :dateTime
                    order by date_time
                    limit :count
                ) t
            )
            """, nativeQuery = true)
    int deleteByDateTimeBefore(LocalDateTime dateTime, int count);
}
