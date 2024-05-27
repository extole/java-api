package com.extole.api.report_runner;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.report.Report.ReportFormat;

@Schema
public interface ReportRunner {

    String getName();

    ReportFormat[] getFormats();

    String getType();

    String getReportTypeName();

    String[] getTags();

    @Nullable
    String getSftpServerId();

    String getCreatedDate();

    String getUpdatedDate();

    boolean isPaused();
}
