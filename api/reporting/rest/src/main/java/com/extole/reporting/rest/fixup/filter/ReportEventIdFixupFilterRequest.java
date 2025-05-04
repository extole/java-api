package com.extole.reporting.rest.fixup.filter;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportEventIdFixupFilterRequest {
    private static final String JSON_REPORT_EVENT_ID = "report_event_id";
    private static final String JSON_EVENT_ID_ATTRIBUTE_NAME = "event_id_attribute_name";

    private final String reportEventId;
    private final String eventIdAttributeName;

    @JsonCreator
    public ReportEventIdFixupFilterRequest(
        @JsonProperty(JSON_REPORT_EVENT_ID) String reportEventId,
        @Nullable @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME) String eventIdAttributeName) {
        this.reportEventId = reportEventId;
        this.eventIdAttributeName = eventIdAttributeName;
    }

    @JsonProperty(JSON_REPORT_EVENT_ID)
    public String getReportEventId() {
        return reportEventId;
    }

    @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME)
    public String getEventIdAttributeName() {
        return eventIdAttributeName;
    }
}
