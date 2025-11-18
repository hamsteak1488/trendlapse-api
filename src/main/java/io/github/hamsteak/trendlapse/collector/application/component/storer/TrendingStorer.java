package io.github.hamsteak.trendlapse.collector.application.component.storer;

import io.github.hamsteak.trendlapse.collector.application.dto.TrendingItem;
import io.github.hamsteak.trendlapse.trending.application.component.TrendingCreator;
import io.github.hamsteak.trendlapse.trending.application.dto.TrendingCreateDto;
import io.github.hamsteak.trendlapse.video.application.component.VideoFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendingStorer {
    private final TrendingCreator trendingCreator;
    private final VideoFinder videoFinder;

    public int store(List<TrendingItem> trendingItems) {
        int storedCount = storeFromTrendingItems(trendingItems);
        log.info("Stored {} trendings.", storedCount);

        return storedCount;
    }

    private int storeFromTrendingItems(List<TrendingItem> trendingItems) {
        List<TrendingCreateDto> dtos = getVideoMissingExcludedTrendingItems(trendingItems).stream()
                .map(item -> new TrendingCreateDto(item.getDateTime(), item.getVideoYoutubeId(), item.getRank(), item.getRegionCode()))
                .toList();

        return trendingCreator.create(dtos);
    }

    private List<TrendingItem> getVideoMissingExcludedTrendingItems(List<TrendingItem> trendingItems) {
        List<String> videoYoutubeIds = trendingItems.stream().map(TrendingItem::getVideoYoutubeId).distinct().toList();
        List<String> missingVideoYoutubeIds = videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds);

        List<TrendingItem> videoMissingTrendingItems = trendingItems.stream()
                .filter(trendingItem -> missingVideoYoutubeIds.contains(trendingItem.getVideoYoutubeId()))
                .toList();
        videoMissingTrendingItems.forEach(trendingItem ->
                log.info("Skipping trending record creation: No matching video found (region={}, rank={}, videoYoutubeId={}).",
                        trendingItem.getRegionCode(), trendingItem.getRank(), trendingItem.getVideoYoutubeId())
        );

        return trendingItems.stream()
                .filter(trendingItem -> !missingVideoYoutubeIds.contains(trendingItem.getVideoYoutubeId()))
                .toList();
    }
}
