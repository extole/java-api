package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface SqlReportType extends ReportType {
    String getDatabase();

    String getQuery();

    String getCreatedDate();

    String getUpdatedDate();

    String getExecutorType();
}
