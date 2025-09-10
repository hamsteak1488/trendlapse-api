package io.github.hamsteak.trendlapse.trending.domain;

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

    public List<DateTimeTrendingDetailList> find(String regionCode, List<LocalDateTime> dateTimes) {
        List<DateTimeTrendingDetailList> dateTimeTrendingDetailLists = new ArrayList<>();

        String cacheName = "trendingsByDateTime";
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache not found.");
        }

        List<LocalDateTime> missingDateTimes = new ArrayList<>();

        for (LocalDateTime dateTime : dateTimes) {
            String cacheKey = getCacheKey(regionCode, dateTime);
            DateTimeTrendingDetailList dateTimeTrendingDetailList = cache.get(cacheKey, DateTimeTrendingDetailList.class);

            if (dateTimeTrendingDetailList == null) {
                log.debug("[Cache Miss] name={}, key={}", cacheName, cacheKey);
                missingDateTimes.add(dateTime);
            } else {
                log.debug("[Cache Hit] name={}, key={}", cacheName, cacheKey);
                dateTimeTrendingDetailLists.add(dateTimeTrendingDetailList);
            }
        }

        List<DateTimeTrendingDetailList> loadedDateTimeTrendingDetailLists = trendingRepository.findDetailByRegionAndDateTimeIn(regionCode, missingDateTimes).stream()
                .collect(Collectors.groupingBy(TrendingDetail::getDateTime)).entrySet().stream()
                .map(entry -> new DateTimeTrendingDetailList(entry.getKey(), entry.getValue()))
                .toList();

        for (DateTimeTrendingDetailList loadedDateTimeTrendingDetailList : loadedDateTimeTrendingDetailLists) {
            cache.put(getCacheKey(regionCode, loadedDateTimeTrendingDetailList.getDateTime()), loadedDateTimeTrendingDetailList);
        }

        dateTimeTrendingDetailLists.addAll(loadedDateTimeTrendingDetailLists);
        dateTimeTrendingDetailLists.sort(Comparator.comparing(DateTimeTrendingDetailList::getDateTime));

        return dateTimeTrendingDetailLists;
    }

    private static String getCacheKey(String regionCode, LocalDateTime dateTime) {
        return regionCode + ":" + dateTime;
    }
}
