package com.extole.reporting.rest.fixup.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class CrossClientReportEventIdFixupFilterResponse extends FixupFilterResponse {
    private static final String JSON_REPORT_EVENT_ID = "report_event_id";
    private static final String JSON_EVENT_ID_ATTRIBUTE_NAME = "event_id_attribute_name";
    private static final String JSON_CLIENT_ID_ATTRIBUTE_NAME = "client_id_attribute_name";

    private final String reportEventId;
    private final String eventIdAttributeName;
    private final String clientIdAttributeName;

    @JsonCreator
    public CrossClientReportEventIdFixupFilterResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupFilterType type,
        @JsonProperty(JSON_REPORT_EVENT_ID) String reportEventId,
        @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME) String eventIdAttributeName,
        @JsonProperty(JSON_CLIENT_ID_ATTRIBUTE_NAME) String clientIdAttributeName) {
        super(id, type);
        this.reportEventId = reportEventId;
        this.eventIdAttributeName = eventIdAttributeName;
        this.clientIdAttributeName = clientIdAttributeName;
    }

    @JsonProperty(JSON_REPORT_EVENT_ID)
    public String getReportEventId() {
        return reportEventId;
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
