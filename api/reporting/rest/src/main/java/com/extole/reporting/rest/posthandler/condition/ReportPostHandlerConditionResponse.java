package com.extole.reporting.rest.posthandler.condition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.posthandler.ConditionType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportPostHandlerConditionResponse.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MatchReportScheduleReportPostHandlerConditionResponse.class,
        name = MatchReportScheduleReportPostHandlerConditionResponse.TYPE),
    @JsonSubTypes.Type(value = MatchReportTagsPostHandlerConditionResponse.class,
        name = MatchReportTagsPostHandlerConditionResponse.TYPE)
})
public abstract class ReportPostHandlerConditionResponse {

    protected static final String JSON_ID = "id";
    protected static final String JSON_TYPE = "type";

    protected final String conditionId;
    protected final ConditionType type;

    public ReportPostHandlerConditionResponse(@JsonProperty(JSON_ID) String conditionId,
        @JsonProperty(JSON_TYPE) ConditionType type) {
        this.conditionId = conditionId;
        this.type = type;
    }

    @JsonProperty(JSON_TYPE)
    public ConditionType getType() {
        return type;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return conditionId;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
