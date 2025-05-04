package com.extole.client.rest.impl.prehandler.condition.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.client.rest.prehandler.condition.exception.ExpressionPrehandlerConditionRestException;
import com.extole.client.rest.prehandler.condition.request.ExpressionPrehandlerConditionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.condition.ExpressionPrehandlerConditionBuilder;
import com.extole.model.service.prehandler.condition.exception.InvalidExpressionPrehandlerConditionException;
import com.extole.model.service.prehandler.condition.exception.MissingExpressionPrehandlerConditionException;

@Component
public class ExpressionPrehandlerConditionRequestMapper
    implements PrehandlerConditionRequestMapper<ExpressionPrehandlerConditionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, ExpressionPrehandlerConditionRequest condition)
        throws PrehandlerConditionValidationRestException {
        try {
            ExpressionPrehandlerConditionBuilder builder =
                prehandlerBuilder.addCondition(com.extole.model.entity.prehandler.PrehandlerConditionType.EXPRESSION);
            builder.withExpression(condition.getExpression());
            builder.done();
        } catch (InvalidExpressionPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionPrehandlerConditionRestException.class)
                .withErrorCode(ExpressionPrehandlerConditionRestException.PREHANDLER_CONDITION_EXPRESSION_INVALID)
                .withCause(e).build();
        } catch (MissingExpressionPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(ExpressionPrehandlerConditionRestException.class)
                .withErrorCode(ExpressionPrehandlerConditionRestException.PREHANDLER_CONDITION_EXPRESSION_MISSING)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.EXPRESSION;
    }
}
