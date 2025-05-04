package com.extole.client.rest.impl.prehandler.condition.request;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.client.rest.prehandler.condition.request.PrehandlerConditionRequest;
import com.extole.model.service.prehandler.PrehandlerBuilder;

public interface PrehandlerConditionRequestMapper<C extends PrehandlerConditionRequest> {

    void update(PrehandlerBuilder prehandlerBuilder, C condition) throws PrehandlerConditionValidationRestException;

    PrehandlerConditionType getType();
}
