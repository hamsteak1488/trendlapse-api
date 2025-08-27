package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Primary
@Component
@RequiredArgsConstructor
public class TrendingSearcherV2 implements TrendingSearcher {
    private final TrendingRepository trendingRepository;
    private final CacheDateTimeTrendingDetailListFinder cacheDateTimeTrendingDetailListFinder;

    @Transactional(readOnly = true)
    public List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter) {
        List<DateTimeTrendingDetailList> dateTimeTrendingDetailLists = new ArrayList<>();
        List<LocalDateTime> dateTimes = trendingRepository.findDateTimes(filter.getRegionCode(), filter.getStartDateTime(), filter.getEndDateTime());

        TreeMap<LocalDate, List<LocalDateTime>> dayDateTimesMap = dateTimes.stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, TreeMap::new, Collectors.toList()));

        LocalDate firstDate = dayDateTimesMap.firstKey();
        LocalDate lastDate = dayDateTimesMap.lastKey();

        for (Map.Entry<LocalDate, List<LocalDateTime>> dayDateTimesEntry : dayDateTimesMap.entrySet()) {
            LocalDate dayDate = dayDateTimesEntry.getKey();
            List<LocalDateTime> dayDateTimes = dayDateTimesEntry.getValue();

            if (!dayDate.equals(firstDate) && !dayDate.equals(lastDate)) {
                List<DateTimeTrendingDetailList> dayDateTimeTrendingDetailLists = cacheDateTimeTrendingDetailListFinder.find(filter.getRegionCode(), dayDate, dayDateTimes);
                dateTimeTrendingDetailLists.addAll(dayDateTimeTrendingDetailLists);
            } else {
                dateTimeTrendingDetailLists.addAll(trendingRepository.findDetailByRegionAndDateTime(filter.getRegionCode(), filter.getStartDateTime(), filter.getEndDateTime()).stream()
                        .collect(Collectors.groupingBy(TrendingDetail::getDateTime, LinkedHashMap::new, Collectors.toList()))
                        .entrySet().stream()
                        .map(mapFromTrendingDetailMapToDateTimeTrendingDetailList())
                        .toList());
            }
        }

        return dateTimeTrendingDetailLists;
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
