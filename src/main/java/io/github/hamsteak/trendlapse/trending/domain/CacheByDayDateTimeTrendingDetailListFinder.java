package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheByDayDateTimeTrendingDetailListFinder {
    private final TrendingRepository trendingRepository;

    @Cacheable(value = "trendingsByDay", key = "#regionCode + ':' + #dayDate")
    public List<DateTimeTrendingDetailList> find(String regionCode, LocalDate dayDate, List<LocalDateTime> dayDateTimes) {

        return trendingRepository.findDetailByRegionAndDateTimeBetween(regionCode, dayDateTimes.get(0), dayDateTimes.get(dayDateTimes.size() - 1)).stream()
                .collect(Collectors.groupingBy(TrendingDetail::getDateTime)).entrySet().stream()
                .map(entry -> new DateTimeTrendingDetailList(entry.getKey(), entry.getValue()))
                .toList();
    }
}
