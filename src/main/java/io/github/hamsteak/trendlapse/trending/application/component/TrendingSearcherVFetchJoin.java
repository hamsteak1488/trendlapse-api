package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.trending.application.dto.DateTimeTrendingDetailList;
import io.github.hamsteak.trendlapse.trending.domain.Trending;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingSearchFilter;
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
public class TrendingSearcherVFetchJoin implements TrendingSearcher {
    private final TrendingRepository trendingRepository;
    private final TrendingDetailReader trendingDetailReader;

    @Transactional(readOnly = true)
    public List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter) {
        return trendingRepository.findWithByRegionAndDateTimeBetweenFetchJoin(filter.getRegionCode(), filter.getStartDateTime(), filter.getEndDateTime()).stream()
                .map(Trending::getId)
                .map(trendingDetailReader::read)
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
