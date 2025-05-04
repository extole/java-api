package com.extole.reporting.rest.impl.posthandler.action;

import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.JavascriptFunction;
import com.extole.reporting.entity.report.posthandler.action.ReportPostHandlerAction;
import com.extole.reporting.rest.posthandler.ActionType;
import com.extole.reporting.rest.posthandler.ReportPostHandlerActionValidationRestException;
import com.extole.reporting.rest.posthandler.action.JavascriptReportPostHandlerActionRequest;
import com.extole.reporting.rest.posthandler.action.exception.JavascriptReportPostHandlerActionValidationRestException;
import com.extole.reporting.service.posthandler.InvalidJavascriptReportPostHandlerActionException;
import com.extole.reporting.service.posthandler.MissingJavascriptReportPostHandlerActionException;
import com.extole.reporting.service.posthandler.ReportPostHandlerBuilder;
import com.extole.reporting.service.posthandler.action.JavascriptReportPostHandlerActionBuilder;

@Component
public class JavascriptReportPostHandlerActionRequestMapper
    implements ReportPostHandlerActionRequestMapper<JavascriptReportPostHandlerActionRequest> {
    @Override
    public void upload(ReportPostHandlerBuilder builder, JavascriptReportPostHandlerActionRequest request)
        throws ReportPostHandlerActionValidationRestException {
        JavascriptReportPostHandlerActionBuilder actionBuilder =
            builder.addAction(ReportPostHandlerAction.ActionType.JAVASCRIPT);

        try {
            actionBuilder.withAction(new JavascriptFunction<>(request.getJavascript().getValue())).done();
        } catch (MissingJavascriptReportPostHandlerActionException e) {
            throw RestExceptionBuilder
                .newBuilder(JavascriptReportPostHandlerActionValidationRestException.class)
                .withErrorCode(JavascriptReportPostHandlerActionValidationRestException.JAVASCRIPT_ACTION_MISSING)
                .withCause(e)
                .build();
        } catch (InvalidJavascriptReportPostHandlerActionException e) {
            throw RestExceptionBuilder
                .newBuilder(JavascriptReportPostHandlerActionValidationRestException.class)
                .withErrorCode(JavascriptReportPostHandlerActionValidationRestException.JAVASCRIPT_ACTION_INVALID)
                .addParameter("validation_errors", e.getValidationErrors())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.JAVASCRIPT;
    }
}
