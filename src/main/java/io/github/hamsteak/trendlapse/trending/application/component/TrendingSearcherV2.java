package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.trending.application.dto.DateTimeTrendingDetailList;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrendingSearcherV2 implements TrendingSearcher {
    private final TrendingRepository trendingRepository;
    private final CacheByDayDateTimeTrendingDetailListFinder cacheByDayDateTimeTrendingDetailListFinder;

    public List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter) {
        List<DateTimeTrendingDetailList> dateTimeTrendingDetailLists = new ArrayList<>();
        List<LocalDateTime> dateTimes = trendingRepository.findDateTimes(filter.getRegionCode(), filter.getStartDateTime(), filter.getEndDateTime());

        if (dateTimes.isEmpty()) {
            return List.of();
        }

        TreeMap<LocalDate, List<LocalDateTime>> dayDateTimesMap = dateTimes.stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, TreeMap::new, Collectors.toList()));

        LocalDate firstDate = dayDateTimesMap.firstKey();
        LocalDate lastDate = dayDateTimesMap.lastKey();

        // first date
        List<LocalDateTime> firstDayDateTimes = dayDateTimesMap.get(firstDate);
        dateTimeTrendingDetailLists.addAll(getDateTimeTrendingDetailLists(filter.getRegionCode(), firstDayDateTimes.get(0), firstDate.atTime(23, 59, 59)));

        if (!lastDate.equals(firstDate)) {
            List<LocalDateTime> lastDayDateTimes = dayDateTimesMap.get(lastDate);
            dateTimeTrendingDetailLists.addAll(getDateTimeTrendingDetailLists(filter.getRegionCode(), lastDate.atTime(0, 0), lastDayDateTimes.get(lastDayDateTimes.size() - 1)));
        }

        for (Map.Entry<LocalDate, List<LocalDateTime>> dayDateTimesEntry : dayDateTimesMap.entrySet()) {
            LocalDate dayDate = dayDateTimesEntry.getKey();
            List<LocalDateTime> dayDateTimes = dayDateTimesEntry.getValue();

            if (dayDate.equals(firstDate) || dayDate.equals(lastDate)) {
                continue;
            }

            List<DateTimeTrendingDetailList> dayDateTimeTrendingDetailLists = cacheByDayDateTimeTrendingDetailListFinder.find(filter.getRegionCode(), dayDate, dayDateTimes);
            dateTimeTrendingDetailLists.addAll(dayDateTimeTrendingDetailLists);
        }

        dateTimeTrendingDetailLists.sort(Comparator.comparing(DateTimeTrendingDetailList::getDateTime));

        return dateTimeTrendingDetailLists;
    }

    private List<DateTimeTrendingDetailList> getDateTimeTrendingDetailLists(String regionCode, LocalDateTime left, LocalDateTime right) {
        return trendingRepository.findDetailByRegionAndDateTimeBetween(regionCode, left, right).stream()
                .collect(Collectors.groupingBy(TrendingDetail::getDateTime, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(mapFromTrendingDetailMapToDateTimeTrendingDetailList())
                .toList();
    }

    private Function<Map.Entry<LocalDateTime, List<TrendingDetail>>, DateTimeTrendingDetailList> mapFromTrendingDetailMapToDateTimeTrendingDetailList() {
        return entry -> {
            LocalDateTime dateTime = entry.getKey();
            List<TrendingDetail> trendingDetails = entry.getValue();

            return DateTimeTrendingDetailList.builder()
                    .dateTime(dateTime)
                    .items(trendingDetails)
                    .build();
        };
    }
}
