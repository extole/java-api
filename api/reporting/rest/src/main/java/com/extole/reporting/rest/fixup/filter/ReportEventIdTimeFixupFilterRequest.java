package com.extole.reporting.rest.fixup.filter;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ReportEventIdTimeFixupFilterRequest {
    private static final String JSON_REPORT_ID = "report_id";
    private static final String JSON_EVENT_ID_ATTRIBUTE_NAME = "event_id_attribute_name";
    private static final String JSON_EVENT_TIME_ATTRIBUTE_NAME = "event_time_attribute_name";

    private final String reportId;
    private final String eventIdAttributeName;
    private final String eventTimeAttributeName;

    @JsonCreator
    public ReportEventIdTimeFixupFilterRequest(
        @JsonProperty(JSON_REPORT_ID) String reportId,
        @Nullable @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME) String eventIdAttributeName,
        @Nullable @JsonProperty(JSON_EVENT_TIME_ATTRIBUTE_NAME) String eventTimeAttributeName) {
        this.reportId = reportId;
        this.eventIdAttributeName = eventIdAttributeName;
        this.eventTimeAttributeName = eventTimeAttributeName;
    }

    @JsonProperty(JSON_REPORT_ID)
    public String getReportId() {
        return reportId;
    }

    @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME)
    public String getEventIdAttributeName() {
        return eventIdAttributeName;
    }

    @JsonProperty(JSON_EVENT_TIME_ATTRIBUTE_NAME)
    public String getEventTimeAttributeName() {
        return eventTimeAttributeName;
    }
}
