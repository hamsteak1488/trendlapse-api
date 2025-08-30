package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrendingSearcherV1 implements TrendingSearcher {
    private final TrendingRepository trendingRepository;

    @Transactional(readOnly = true)
    public List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter) {
        return trendingRepository.findDetailByRegionAndDateTimeBetween(filter.getRegionCode(), filter.getStartDateTime(), filter.getEndDateTime()).stream()
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
