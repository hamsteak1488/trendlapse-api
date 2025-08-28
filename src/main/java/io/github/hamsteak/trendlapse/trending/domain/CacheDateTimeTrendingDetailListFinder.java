package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheDateTimeTrendingDetailListFinder {
    private final TrendingRepository trendingRepository;

    @Cacheable(value = "trendingsByDay", key = "#regionCode + ':' + #dayDate")
    public List<DateTimeTrendingDetailList> find(String regionCode, LocalDate dayDate, List<LocalDateTime> dayDateTimes) {
        List<DateTimeTrendingDetailList> dayDateTimeTrendingDetailLists = new ArrayList<>();

        for (LocalDateTime dateTime : dayDateTimes) {
            List<TrendingDetail> trendingDetails = trendingRepository.findDetailByRegionAndDateTime(regionCode, dateTime);

            dayDateTimeTrendingDetailLists.add(DateTimeTrendingDetailList.builder()
                    .dateTime(dateTime)
                    .items(trendingDetails)
                    .build());
        }

        return dayDateTimeTrendingDetailLists;
    }
}
