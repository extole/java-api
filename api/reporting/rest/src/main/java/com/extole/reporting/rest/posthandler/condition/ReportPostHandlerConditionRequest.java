package com.extole.reporting.rest.posthandler.condition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.posthandler.ConditionType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportPostHandlerConditionRequest.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MatchReportScheduleReportPostHandlerConditionRequest.class,
        name = MatchReportScheduleReportPostHandlerConditionRequest.TYPE),
    @JsonSubTypes.Type(value = MatchReportTagsPostHandlerConditionRequest.class,
        name = MatchReportTagsPostHandlerConditionRequest.TYPE),
})
public abstract class ReportPostHandlerConditionRequest {

    protected static final String JSON_TYPE = "type";
    protected final ConditionType type;

    protected ReportPostHandlerConditionRequest(@JsonProperty(JSON_TYPE) ConditionType type) {
        this.type = type;
    }

    @JsonProperty(JSON_TYPE)
    public ConditionType getType() {
        return type;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
