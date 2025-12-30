package io.github.hamsteak.trendlapse.region.application.dto;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionView {
    @NonNull
    private final String regionId;

    @NonNull
    private final String name;
}
