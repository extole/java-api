package com.extole.reporting.rest.posthandler.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.posthandler.ActionType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportPostHandlerActionResponse.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = JavascriptReportPostHandlerActionResponse.class,
        name = JavascriptReportPostHandlerActionResponse.TYPE)
})
public abstract class ReportPostHandlerActionResponse {

    protected static final String JSON_ID = "id";
    protected static final String JSON_TYPE = "type";

    private final String id;
    private final ActionType actionType;

    public ReportPostHandlerActionResponse(@JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_TYPE) ActionType actionType) {
        this.id = id;
        this.actionType = actionType;
    }

    @JsonProperty(JSON_TYPE)
    public ActionType getActionType() {
        return actionType;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
