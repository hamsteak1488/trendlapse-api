package io.github.hamsteak.trendlapse.trending.video.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class SnapshotNotFoundException extends DomainException {
    public SnapshotNotFoundException(String message) {
        super(DomainError.SNAPSHOT_NOT_FOUND, message);
    }
}
