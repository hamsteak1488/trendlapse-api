package io.github.hamsteak.trendlapse.trending.application.component;

import io.github.hamsteak.trendlapse.trending.application.dto.DateTimeTrendingDetailList;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingDetail;
import io.github.hamsteak.trendlapse.trending.infrastructure.TrendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheByDateTimeDateTimeTrendingDetailListFinder {
    private final CacheManager cacheManager;
    private final TrendingRepository trendingRepository;

    private final String CACHE_NAME = "trendingsByDateTime";

    public List<DateTimeTrendingDetailList> find(String regionCode, List<LocalDateTime> dateTimes) {
        List<DateTimeTrendingDetailList> dateTimeTrendingDetailLists = new ArrayList<>();

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) {
            throw new IllegalStateException("Cache not found.");
        }

        List<LocalDateTime> missingDateTimes = new ArrayList<>();

        for (LocalDateTime dateTime : dateTimes) {
            String cacheKey = getCacheKey(regionCode, dateTime);
            DateTimeTrendingDetailList dateTimeTrendingDetailList = cache.get(cacheKey, DateTimeTrendingDetailList.class);

            if (dateTimeTrendingDetailList == null) {
                missingDateTimes.add(dateTime);
            } else {
                dateTimeTrendingDetailLists.add(dateTimeTrendingDetailList);
            }
        }

        if (!missingDateTimes.isEmpty()) {
            List<DateTimeTrendingDetailList> loadedDateTimeTrendingDetailLists = trendingRepository.findDetailByRegionAndDateTimeIn(regionCode, missingDateTimes).stream()
                    .collect(Collectors.groupingBy(TrendingDetail::getDateTime)).entrySet().stream()
                    .map(entry -> new DateTimeTrendingDetailList(entry.getKey(), entry.getValue()))
                    .toList();

            for (DateTimeTrendingDetailList loadedDateTimeTrendingDetailList : loadedDateTimeTrendingDetailLists) {
                cache.put(getCacheKey(regionCode, loadedDateTimeTrendingDetailList.getDateTime()), loadedDateTimeTrendingDetailList);
            }

            dateTimeTrendingDetailLists.addAll(loadedDateTimeTrendingDetailLists);
        }

        dateTimeTrendingDetailLists.sort(Comparator.comparing(DateTimeTrendingDetailList::getDateTime));

        return dateTimeTrendingDetailLists;
    }

    private static String getCacheKey(String regionCode, LocalDateTime dateTime) {
        return regionCode + ":" + dateTime;
    }
}
