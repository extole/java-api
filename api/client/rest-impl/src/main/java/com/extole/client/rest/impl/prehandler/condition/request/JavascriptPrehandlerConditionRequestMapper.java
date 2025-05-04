package com.extole.client.rest.impl.prehandler.condition.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.client.rest.prehandler.condition.exception.JavascriptPrehandlerConditionRestException;
import com.extole.client.rest.prehandler.condition.request.JavascriptPrehandlerConditionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.JavascriptFunction;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.condition.JavascriptPrehandlerConditionBuilder;
import com.extole.model.service.prehandler.condition.exception.InvalidJavascriptPrehandlerConditionException;
import com.extole.model.service.prehandler.condition.exception.MissingJavascriptPrehandlerConditionException;

@Component
public class JavascriptPrehandlerConditionRequestMapper
    implements PrehandlerConditionRequestMapper<JavascriptPrehandlerConditionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, JavascriptPrehandlerConditionRequest condition)
        throws PrehandlerConditionValidationRestException {
        try {
            JavascriptPrehandlerConditionBuilder builder = prehandlerBuilder
                .addCondition(com.extole.model.entity.prehandler.PrehandlerConditionType.JAVASCRIPT_V1);
            if (condition.getJavascript() != null) {
                builder.withJavascript(new JavascriptFunction<>(condition.getJavascript().getValue()));
            }
            builder.done();
        } catch (MissingJavascriptPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(JavascriptPrehandlerConditionRestException.class)
                .withErrorCode(JavascriptPrehandlerConditionRestException.PREHANDLER_CONDITION_JAVASCRIPT_MISSING)
                .withCause(e).build();
        } catch (InvalidJavascriptPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(JavascriptPrehandlerConditionRestException.class)
                .withErrorCode(JavascriptPrehandlerConditionRestException.PREHANDLER_CONDITION_JAVASCRIPT_INVALID)
                .addParameter("output", e.getOutput())
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.JAVASCRIPT_V1;
    }
}
