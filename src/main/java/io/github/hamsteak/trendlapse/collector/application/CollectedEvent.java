package io.github.hamsteak.trendlapse.collector.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CollectedEvent {
    private final List<Long> collectedSnapshotIds;
}
