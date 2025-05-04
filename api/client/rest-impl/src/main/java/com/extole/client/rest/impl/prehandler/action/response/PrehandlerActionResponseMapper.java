package com.extole.client.rest.impl.prehandler.action.response;

import com.extole.client.rest.prehandler.action.response.PrehandlerActionResponse;
import com.extole.model.entity.prehandler.PrehandlerAction;
import com.extole.model.entity.prehandler.PrehandlerActionType;

public interface PrehandlerActionResponseMapper<A extends PrehandlerAction, R extends PrehandlerActionResponse> {

    R toResponse(A action);

    PrehandlerActionType getType();
}
