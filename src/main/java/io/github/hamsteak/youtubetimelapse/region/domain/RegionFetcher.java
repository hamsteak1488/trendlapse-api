package io.github.hamsteak.youtubetimelapse.region.domain;

import io.github.hamsteak.youtubetimelapse.external.youtube.RegionApiCaller;
import io.github.hamsteak.youtubetimelapse.external.youtube.dto.RegionListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegionFetcher {
    private final RegionApiCaller regionApiCaller;
    private final RegionCreator regionCreator;

    public void fetch() {
        RegionListResponse response = regionApiCaller.fetchRegions();

        response.getItems()
                .forEach(item ->
                        regionCreator.create(item.getId(), item.getSnippet().getName(), item.getSnippet().getGl())
                );
    }
}
