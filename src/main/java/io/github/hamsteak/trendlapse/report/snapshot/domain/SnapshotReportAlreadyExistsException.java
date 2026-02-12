package io.github.hamsteak.trendlapse.report.snapshot.domain;

import io.github.hamsteak.trendlapse.global.error.DomainError;
import io.github.hamsteak.trendlapse.global.error.DomainException;

public class SnapshotReportAlreadyExistsException extends DomainException {
    public SnapshotReportAlreadyExistsException(String message) {
        super(DomainError.SNAPSHOT_REPORT_ALREADY_EXISTS, message);
    }
}
