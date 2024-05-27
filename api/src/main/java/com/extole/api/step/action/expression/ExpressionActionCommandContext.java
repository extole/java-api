package com.extole.api.step.action.expression;

import java.util.Map;

import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.step.action.AsyncActionContext;

public interface ExpressionActionCommandContext extends AsyncActionContext {

    Map<String, Object> getData();

    InternalConsumerEventBuilder internalConsumerEventBuilder();

}
