package io.github.hamsteak.trendlapse.report.snapshot.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class SnapshotReportNotFoundException extends DomainException {
    public SnapshotReportNotFoundException(String message) {
        super(DomainError.SNAPSHOT_REPORT_NOT_FOUND, message);
    }
}
