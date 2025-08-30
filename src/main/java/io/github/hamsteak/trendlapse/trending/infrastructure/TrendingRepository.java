package io.github.hamsteak.trendlapse.trending.infrastructure;

import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.domain.TrendingDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrendingRepository extends Repository<Trending, Long> {
    Trending save(Trending trending);

    Optional<Trending> findById(long id);

    List<Trending> findByDateTime(LocalDateTime dateTime);

    @Query("select distinct t.dateTime from Trending t where t.region.regionCode = :regionCode and t.dateTime between :startDateTime and :endDateTime")
    List<LocalDateTime> findDateTimes(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("select t from Trending t where t.region.regionCode = :regionCode and t.dateTime between :startDateTime and :endDateTime")
    List<Trending> findByRegionAndDateTime(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query(
            """
                    select
                        new io.github.hamsteak.trendlapse.trending.domain.TrendingDetail(
                            t.dateTime,
                            t.rankValue,
                            new io.github.hamsteak.trendlapse.video.domain.VideoDetail(
                                v.id,
                                c.id,
                                v.youtubeId,
                                v.title,
                                v.thumbnailUrl
                            ),
                            new io.github.hamsteak.trendlapse.channel.domain.ChannelDetail(
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
                    """
    )
    List<TrendingDetail> findDetailByRegionAndDateTimeBetween(String regionCode, LocalDateTime startDateTime, LocalDateTime endDateTime);


    @Query(
            """
                    select
                        new io.github.hamsteak.trendlapse.trending.domain.TrendingDetail(
                            t.dateTime,
                            t.rankValue,
                            new io.github.hamsteak.trendlapse.video.domain.VideoDetail(
                                v.id,
                                c.id,
                                v.youtubeId,
                                v.title,
                                v.thumbnailUrl
                            ),
                            new io.github.hamsteak.trendlapse.channel.domain.ChannelDetail(
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
                    """
    )
    List<TrendingDetail> findDetailByRegionAndDateTime(String regionCode, LocalDateTime dateTime);
}
