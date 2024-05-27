package com.extole.api.report;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Report {

    enum ReportFormat {
        JSON,
        CSV,
        PSV,
        XLSX,
        HEADLESS_CSV,
        HEADLESS_PSV
    }

    ReportFormat[] getReportFormats();

    String getId();

    String getName();

    String getDisplayName();

    String[] getTags();

    String getCreatedDate();

    ReportResult getReportResult();

    String getExecutorType();

    @Nullable
    String getSftpServerId();
}
