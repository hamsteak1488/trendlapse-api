package io.github.hamsteak.trendlapse.region.application.component;

import io.github.hamsteak.trendlapse.youtube.infrastructure.YoutubeDataApiCaller;
import io.github.hamsteak.trendlapse.youtube.infrastructure.dto.RegionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionFetcher {
    private final YoutubeDataApiCaller youtubeDataApiCaller;
    private final RegionPutter regionPutter;
    private final RegionReader regionReader;

    public void fetch() {
        RegionListResponse response = youtubeDataApiCaller.fetchRegions();

        response.getItems()
                .forEach(item ->
                        regionPutter.put(item.getId(), item.getSnippet().getName(), item.getSnippet().getGl())
                );

        regionReader.setReady(true);
    }
}
