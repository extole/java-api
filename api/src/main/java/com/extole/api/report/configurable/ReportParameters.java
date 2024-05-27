package com.extole.api.report.configurable;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ReportParameters {
    TimeRange getTimeRange();

    Map<String, String> getValues();
}
