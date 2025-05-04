package com.extole.client.rest.impl.prehandler.condition.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.condition.response.ExpressionPrehandlerConditionResponse;
import com.extole.model.entity.prehandler.PrehandlerConditionType;
import com.extole.model.entity.prehandler.condition.ExpressionPrehandlerCondition;

@Component
public class ExpressionPrehandlerConditionResponseMapper implements
    PrehandlerConditionResponseMapper<ExpressionPrehandlerCondition, ExpressionPrehandlerConditionResponse> {

    @Override
    public ExpressionPrehandlerConditionResponse toResponse(ExpressionPrehandlerCondition condition) {
        return new ExpressionPrehandlerConditionResponse(condition.getId().getValue(), condition.getExpression());
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.EXPRESSION;
    }
}
