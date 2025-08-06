package io.github.hamsteak.trendlapse.region.domain;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionDetail {
    @NonNull
    private final String regionCode;

    @NonNull
    private final String name;
}
