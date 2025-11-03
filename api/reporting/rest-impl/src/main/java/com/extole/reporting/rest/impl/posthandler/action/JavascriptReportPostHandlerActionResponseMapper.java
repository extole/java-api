package com.extole.reporting.rest.impl.posthandler.action;

import org.springframework.stereotype.Component;

import com.extole.id.JavascriptFunction;
import com.extole.reporting.entity.report.posthandler.action.JavascriptReportPostHandlerAction;
import com.extole.reporting.entity.report.posthandler.action.ReportPostHandlerAction.ActionType;
import com.extole.reporting.rest.posthandler.action.JavascriptReportPostHandlerActionResponse;

@Component
public class JavascriptReportPostHandlerActionResponseMapper
    implements
    ReportPostHandlerActionResponseMapper<JavascriptReportPostHandlerAction,
        JavascriptReportPostHandlerActionResponse> {

    @Override
    public JavascriptReportPostHandlerActionResponse toReponse(JavascriptReportPostHandlerAction action) {
        return new JavascriptReportPostHandlerActionResponse(action.getId().getValue(),
            new JavascriptFunction<>(action.getJavascript().getValue()));
    }

    @Override
    public ActionType getType() {
        return ActionType.JAVASCRIPT;
    }
}
