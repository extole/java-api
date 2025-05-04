package com.extole.client.rest.impl.prehandler.action.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.client.rest.prehandler.action.exception.ExpressionPrehandlerActionRestException;
import com.extole.client.rest.prehandler.action.request.ExpressionPrehandlerActionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.action.expression.ExpressionPrehandlerActionBuilder;
import com.extole.model.service.prehandler.action.expression.InvalidExpressionPrehandlerActionException;
import com.extole.model.service.prehandler.action.expression.MissingExpressionPrehandlerActionException;

@Component
public class ExpressionPrehandlerActionRequestMapper
    implements PrehandlerActionRequestMapper<ExpressionPrehandlerActionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, ExpressionPrehandlerActionRequest action)
        throws PrehandlerActionValidationRestException {
        try {
            ExpressionPrehandlerActionBuilder builder =
                prehandlerBuilder.addAction(com.extole.model.entity.prehandler.PrehandlerActionType.EXPRESSION);
            builder.withExpression(action.getExpression());
            builder.done();
        } catch (InvalidExpressionPrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionPrehandlerActionRestException.class)
                .withErrorCode(ExpressionPrehandlerActionRestException.PREHANDLER_ACTION_EXPRESSION_INVALID)
                .withCause(e).build();
        } catch (MissingExpressionPrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionPrehandlerActionRestException.class)
                .withErrorCode(ExpressionPrehandlerActionRestException.PREHANDLER_ACTION_EXPRESSION_MISSING)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.EXPRESSION;
    }
}
