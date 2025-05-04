package com.extole.reporting.rest.audience.list.response;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.audience.list.AudienceListState;
import com.extole.reporting.rest.audience.list.AudienceListType;

public class StaticAudienceListResponse extends AudienceListResponse {
    static final String AUDIENCE_TYPE = "STATIC";

    private static final String REPORT_ID = "report_id";

    private final String reportId;

    public StaticAudienceListResponse(
        @JsonProperty(ID) String id,
        @JsonProperty(NAME) String name,
        @JsonProperty(STATE) AudienceListState audienceListState,
        @JsonProperty(TAGS) Set<String> tags,
        @JsonProperty(REPORT_ID) String reportId,
        @JsonProperty(DESCRIPTION) Optional<String> description,
        @JsonProperty(EVENT_COLUMNS) Set<String> eventColumns,
        @JsonProperty(EVENT_DATA) Map<String, String> eventData,
        @JsonProperty(MEMBER_COUNT) Optional<Long> memberCount,
        @JsonProperty(LAST_UPDATE) Optional<ZonedDateTime> lastUpdate,
        @JsonProperty(ERROR_CODE) Optional<String> errorCode,
        @JsonProperty(ERROR_MESSAGE) Optional<String> errorMessage) {
        super(AudienceListType.STATIC, id, name, tags, audienceListState, description, eventColumns, eventData,
            memberCount, lastUpdate, errorCode, errorMessage);
        this.reportId = reportId;
    }

    @JsonProperty(REPORT_ID)
    public String getReportId() {
        return reportId;
    }
}
