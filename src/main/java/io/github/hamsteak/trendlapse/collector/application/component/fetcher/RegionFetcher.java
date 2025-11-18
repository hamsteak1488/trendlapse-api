package io.github.hamsteak.trendlapse.collector.application.component.fetcher;

import io.github.hamsteak.trendlapse.collector.application.dto.RegionItem;
import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RegionListResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegionFetcher {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    @Getter
    @Setter
    private int externalRegionCount = 110;

    public List<RegionItem> fetch() {
        RegionListResponse response = youtubeDataApiCaller.fetchRegions();

        List<RegionItem> regionItems = response.getItems().stream()
                .map(item -> new RegionItem(item.getId(), item.getSnippet().getName(), item.getSnippet().getGl()))
                .toList();

        log.info("Fetching regions from external server has been completed.");

        return regionItems;
    }
}
