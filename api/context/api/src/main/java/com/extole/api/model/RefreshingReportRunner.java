package com.extole.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface RefreshingReportRunner extends ReportRunner {
    Long getExpirationDurationMilliseconds();
}
