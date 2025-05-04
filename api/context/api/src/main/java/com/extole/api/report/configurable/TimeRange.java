package com.extole.api.report.configurable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface TimeRange {

    String getStartTime();

    String getEndTime();

    String getTimezone();
}
