package com.extole.reporting.rest.fixup.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportEventIdTimeFixupFilterResponse extends FixupFilterResponse {
    private static final String JSON_REPORT_ID = "report_id";
    private static final String JSON_EVENT_ID_ATTRIBUTE_NAME = "event_id_attribute_name";
    private static final String JSON_EVENT_TIME_ATTRIBUTE_NAME = "event_time_attribute_name";

    private final String reportId;
    private final String eventIdAttributeName;
    private final String eventTimeAttributeName;

    @JsonCreator
    public ReportEventIdTimeFixupFilterResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupFilterType type,
        @JsonProperty(JSON_REPORT_ID) String reportId,
        @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME) String eventIdAttributeName,
        @JsonProperty(JSON_EVENT_TIME_ATTRIBUTE_NAME) String eventTimeAttributeName) {
        super(id, type);
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
