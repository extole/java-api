package com.extole.reporting.rest.fixup.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CrossClientReportEventIdFixupFilterCreateRequest {
    private static final String JSON_REPORT_ID = "report_id";
    private static final String JSON_EVENT_ID_ATTRIBUTE_NAME = "event_id_attribute_name";
    private static final String JSON_CLIENT_ID_ATTRIBUTE_NAME = "client_id_attribute_name";

    private final String reportId;
    private final String eventIdAttributeName;
    private final String clientIdAttributeName;

    @JsonCreator
    public CrossClientReportEventIdFixupFilterCreateRequest(
        @JsonProperty(JSON_REPORT_ID) String reportId,
        @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME) String eventIdAttributeName,
        @JsonProperty(JSON_CLIENT_ID_ATTRIBUTE_NAME) String clientIdAttributeName) {
        this.reportId = reportId;
        this.eventIdAttributeName = eventIdAttributeName;
        this.clientIdAttributeName = clientIdAttributeName;
    }

    @JsonProperty(JSON_REPORT_ID)
    public String getReportId() {
        return reportId;
    }

    @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME)
    public String getEventIdAttributeName() {
        return eventIdAttributeName;
    }

    @JsonProperty(JSON_CLIENT_ID_ATTRIBUTE_NAME)
    public String getClientIdAttributeName() {
        return clientIdAttributeName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
