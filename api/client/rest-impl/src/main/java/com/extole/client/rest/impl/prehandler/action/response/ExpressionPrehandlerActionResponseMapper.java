package com.extole.client.rest.impl.prehandler.action.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.action.response.ExpressionPrehandlerActionResponse;
import com.extole.model.entity.prehandler.PrehandlerActionType;
import com.extole.model.entity.prehandler.action.ExpressionPrehandlerAction;

@Component
public class ExpressionPrehandlerActionResponseMapper
    implements PrehandlerActionResponseMapper<ExpressionPrehandlerAction, ExpressionPrehandlerActionResponse> {

    @Override
    public ExpressionPrehandlerActionResponse toResponse(ExpressionPrehandlerAction action) {
        return new ExpressionPrehandlerActionResponse(action.getId().getValue(), action.getExpression());
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.EXPRESSION;
    }
}
