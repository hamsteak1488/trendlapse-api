package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.global.errors.exception.VideoNotFoundException;
import io.github.hamsteak.trendlapse.trending.application.component.TrendingCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaTrendingStorer implements TrendingStorer {
    private final TrendingCreator trendingCreator;

    @Override
    public int store(List<TrendingItem> trendingItems) {
        int storedCount = storeFromTrendingItems(trendingItems);
        log.info("Stored {} trendings.", storedCount);

        return storedCount;
    }

    private int storeFromTrendingItems(List<TrendingItem> trendingItems) {
        int storedCount = 0;

        for (TrendingItem trendingItem : trendingItems) {
            LocalDateTime dateTime = trendingItem.getDateTime();
            String videoYoutubeId = trendingItem.getVideoYoutubeId();
            int rank = trendingItem.getRank();
            String regionCode = trendingItem.getRegionCode();

            try {
                trendingCreator.create(dateTime, videoYoutubeId, rank, regionCode);
                storedCount++;
            } catch (VideoNotFoundException ex) {
                log.info("Skipping trending record creation: No matching video found (region={}, rank={}, videoYoutubeId={}).",
                        regionCode, rank, videoYoutubeId);
            }
        }

        return storedCount;
    }
}
