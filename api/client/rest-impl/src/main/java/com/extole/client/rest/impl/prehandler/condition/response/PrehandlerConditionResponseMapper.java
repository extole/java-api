package com.extole.client.rest.impl.prehandler.condition.response;

import com.extole.client.rest.prehandler.condition.response.PrehandlerConditionResponse;
import com.extole.model.entity.prehandler.PrehandlerCondition;
import com.extole.model.entity.prehandler.PrehandlerConditionType;

public interface PrehandlerConditionResponseMapper<C extends PrehandlerCondition,
    R extends PrehandlerConditionResponse> {

    R toResponse(C condition);

    PrehandlerConditionType getType();
}
