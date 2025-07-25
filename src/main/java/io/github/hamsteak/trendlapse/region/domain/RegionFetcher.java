package io.github.hamsteak.trendlapse.region.domain;

import io.github.hamsteak.trendlapse.external.youtube.dto.RegionListResponse;
import io.github.hamsteak.trendlapse.external.youtube.infrastructure.RegionApiCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionFetcher {
    private final RegionApiCaller regionApiCaller;
    private final RegionPutter regionPutter;

    public void fetch() {
        RegionListResponse response = regionApiCaller.fetchRegions();

        response.getItems()
                .forEach(item ->
                        regionPutter.put(item.getId(), item.getSnippet().getName(), item.getSnippet().getGl())
                );
    }
}
