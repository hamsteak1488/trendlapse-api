package io.github.hamsteak.youtubetimelapse.region.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.dto.RegionListResponse;
import io.github.hamsteak.youtubetimelapse.external.youtube.infrastructure.RegionApiCaller;
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
