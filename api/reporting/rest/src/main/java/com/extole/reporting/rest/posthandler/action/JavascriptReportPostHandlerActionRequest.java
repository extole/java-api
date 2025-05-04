package com.extole.reporting.rest.posthandler.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.report.ReportPostHandlerActionContext;
import com.extole.id.JavascriptFunction;
import com.extole.reporting.rest.posthandler.ActionType;

public class JavascriptReportPostHandlerActionRequest extends ReportPostHandlerActionRequest {
    static final String TYPE = "JAVASCRIPT";

    private static final String JSON_ACTION = "javascript";

    private final JavascriptFunction<ReportPostHandlerActionContext, Void> javascript;

    @JsonCreator
    public JavascriptReportPostHandlerActionRequest(
        @JsonProperty(JSON_ACTION) JavascriptFunction<ReportPostHandlerActionContext, Void> javascript) {
        super(ActionType.JAVASCRIPT);
        this.javascript = javascript;
    }

    public JavascriptFunction<ReportPostHandlerActionContext, Void> getJavascript() {
        return javascript;
    }

    public static <T> Builder<T> builder(T parent) {
        return new Builder<>(parent);
    }

    public static final class Builder<T> {
        private final T parent;
        private JavascriptFunction<ReportPostHandlerActionContext, Void> action;

        private Builder(T parent) {
            this.parent = parent;
        }

        public Builder<T> withAction(JavascriptFunction<ReportPostHandlerActionContext, Void> action) {
            this.action = action;
            return this;
        }

        public T done() {
            return parent;
        }

        public JavascriptReportPostHandlerActionRequest build() {
            return new JavascriptReportPostHandlerActionRequest(action);
        }
    }
}
