package com.extole.reporting.rest.posthandler.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.report.ReportPostHandlerActionContext;
import com.extole.id.JavascriptFunction;
import com.extole.reporting.rest.posthandler.ActionType;

public class JavascriptReportPostHandlerActionResponse extends ReportPostHandlerActionResponse {
    static final String TYPE = "JAVASCRIPT";

    private static final String JSON_ACTION = "javascript";

    private final JavascriptFunction<ReportPostHandlerActionContext, Void> javascript;

    @JsonCreator
    public JavascriptReportPostHandlerActionResponse(@JsonProperty(JSON_ID) String actionId,
        @JsonProperty(JSON_ACTION) JavascriptFunction<ReportPostHandlerActionContext, Void> javascript) {
        super(actionId, ActionType.JAVASCRIPT);
        this.javascript = javascript;
    }

    @JsonProperty(JSON_ACTION)
    public JavascriptFunction<ReportPostHandlerActionContext, Void> getJavascript() {
        return javascript;
    }
}
