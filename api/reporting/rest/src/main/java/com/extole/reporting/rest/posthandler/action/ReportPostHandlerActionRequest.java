package com.extole.reporting.rest.posthandler.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.posthandler.ActionType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ReportPostHandlerActionRequest.JSON_TYPE)
@JsonSubTypes({
    @JsonSubTypes.Type(value = JavascriptReportPostHandlerActionRequest.class,
        name = JavascriptReportPostHandlerActionRequest.TYPE),
})
public abstract class ReportPostHandlerActionRequest {

    protected static final String JSON_TYPE = "type";

    private final ActionType type;

    protected ReportPostHandlerActionRequest(@JsonProperty(JSON_TYPE) ActionType type) {
        this.type = type;
    }

    @JsonProperty(JSON_TYPE)
    public ActionType getType() {
        return type;
    }

    @Override
    public final String toString() {
        return ToString.create(this);
    }
}
