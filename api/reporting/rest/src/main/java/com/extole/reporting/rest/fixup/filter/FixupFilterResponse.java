package com.extole.reporting.rest.fixup.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = FixupFilterResponse.JSON_TYPE)
@JsonSubTypes({
    @Type(value = EventIdsFixupFilterResponse.class, name = "EVENT_IDS"),
    @Type(value = ReportEventIdFixupFilterResponse.class, name = "REPORT_ID"),
    @Type(value = ReportEventIdTimeFixupFilterResponse.class, name = "REPORT_EVENT_ID_TIME"),
    @Type(value = ProfileIdsFixupFilterResponse.class, name = "PROFILE_IDS")
})
public abstract class FixupFilterResponse {
    protected static final String JSON_ID = "id";
    protected static final String JSON_TYPE = "type";

    private final String id;
    private final FixupFilterType type;

    public FixupFilterResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) FixupFilterType type) {
        this.id = id;
        this.type = type;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_TYPE)
    public FixupFilterType getType() {
        return type;
    }
}
