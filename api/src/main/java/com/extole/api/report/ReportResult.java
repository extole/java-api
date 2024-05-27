package com.extole.api.report;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ReportResult {

    enum Status {
        PENDING, IN_PROGRESS, DONE, FAILED, CANCELED, SFTP_DELIVERY_FAILED, EXPIRED
    }

    /* Returns a List, each item contains a row from the Report. */
    Object[] getData(int offset, int limit);

    Status getStatus();

    long getTotalRows();

    String getCreatedDate();

    @Nullable
    String getStartedDate();

    @Nullable
    String getCompletedDate();
}
