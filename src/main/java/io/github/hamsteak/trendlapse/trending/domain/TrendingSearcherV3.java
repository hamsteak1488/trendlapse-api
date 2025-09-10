package io.github.hamsteak.trendlapse.trending.domain;

import io.github.hamsteak.trendlapse.trending.domain.dto.TrendingSearchFilter;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class TrendingSearcherV3 implements TrendingSearcher {
    private final TrendingRepository trendingRepository;
    private final CacheByDateTimeDateTimeTrendingDetailListFinder cacheByDateTimeDateTimeTrendingDetailListFinder;

    @Transactional(readOnly = true)
    public List<DateTimeTrendingDetailList> search(TrendingSearchFilter filter) {
        List<LocalDateTime> dateTimes = trendingRepository.findDateTimes(filter.getRegionCode(), filter.getStartDateTime(), filter.getEndDateTime());

        if (dateTimes.isEmpty()) {
            return List.of();
        }

        List<DateTimeTrendingDetailList> dateTimeTrendingDetailLists = cacheByDateTimeDateTimeTrendingDetailListFinder.find(filter.getRegionCode(), dateTimes);
        dateTimeTrendingDetailLists.sort(Comparator.comparing(DateTimeTrendingDetailList::getDateTime));

        return dateTimeTrendingDetailLists;
    }
}
