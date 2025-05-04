package com.extole.client.rest.impl.prehandler.action.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.client.rest.prehandler.action.exception.JavascriptPrehandlerActionRestException;
import com.extole.client.rest.prehandler.action.request.JavascriptPrehandlerActionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.JavascriptFunction;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.action.javascript.InvalidJavascriptPrehandlerActionException;
import com.extole.model.service.prehandler.action.javascript.JavascriptPrehandlerActionBuilder;
import com.extole.model.service.prehandler.action.javascript.MissingJavascriptPrehandlerActionException;

@Component
public class JavascriptPrehandlerActionRequestMapper
    implements PrehandlerActionRequestMapper<JavascriptPrehandlerActionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, JavascriptPrehandlerActionRequest action)
        throws PrehandlerActionValidationRestException {
        try {
            JavascriptPrehandlerActionBuilder builder =
                prehandlerBuilder.addAction(com.extole.model.entity.prehandler.PrehandlerActionType.JAVASCRIPT_V1);
            if (action.getJavascript() != null) {
                builder.withJavascript(new JavascriptFunction<>(action.getJavascript().getValue()));
            }
            builder.done();
        } catch (MissingJavascriptPrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(JavascriptPrehandlerActionRestException.class)
                .withErrorCode(JavascriptPrehandlerActionRestException.PREHANDLER_ACTION_JAVASCRIPT_MISSING)
                .withCause(e).build();
        } catch (InvalidJavascriptPrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(JavascriptPrehandlerActionRestException.class)
                .withErrorCode(JavascriptPrehandlerActionRestException.PREHANDLER_ACTION_JAVASCRIPT_INVALID)
                .addParameter("output", e.getOutput())
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.JAVASCRIPT_V1;
    }
}
