package com.extole.reporting.rest.fixup.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class CrossClientReportEventIdFixupFilterUpdateRequest {
    private static final String JSON_REPORT_ID = "report_id";
    private static final String JSON_EVENT_ID_ATTRIBUTE_NAME = "event_id_attribute_name";
    private static final String JSON_CLIENT_ID_ATTRIBUTE_NAME = "client_id_attribute_name";

    private final Omissible<String> reportId;
    private final Omissible<String> eventIdAttributeName;
    private final Omissible<String> clientIdAttributeName;

    @JsonCreator
    public CrossClientReportEventIdFixupFilterUpdateRequest(
        @JsonProperty(JSON_REPORT_ID) Omissible<String> reportId,
        @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME) Omissible<String> eventIdAttributeName,
        @JsonProperty(JSON_CLIENT_ID_ATTRIBUTE_NAME) Omissible<String> clientIdAttributeName) {
        this.reportId = reportId;
        this.eventIdAttributeName = eventIdAttributeName;
        this.clientIdAttributeName = clientIdAttributeName;
    }

    @JsonProperty(JSON_REPORT_ID)
    public Omissible<String> getReportId() {
        return reportId;
    }

    @JsonProperty(JSON_EVENT_ID_ATTRIBUTE_NAME)
    public Omissible<String> getEventIdAttributeName() {
        return eventIdAttributeName;
    }

    @JsonProperty(JSON_CLIENT_ID_ATTRIBUTE_NAME)
    public Omissible<String> getClientIdAttributeName() {
        return clientIdAttributeName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
