package com.extole.client.rest.impl.prehandler.action.request;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.client.rest.prehandler.action.request.PrehandlerActionRequest;
import com.extole.model.service.prehandler.PrehandlerBuilder;

public interface PrehandlerActionRequestMapper<A extends PrehandlerActionRequest> {

    void update(PrehandlerBuilder prehandlerBuilder, A action) throws PrehandlerActionValidationRestException;

    PrehandlerActionType getType();
}
