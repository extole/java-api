package com.extole.client.rest.impl.prehandler.action.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.action.response.SetSandboxPrehandlerActionResponse;
import com.extole.model.entity.prehandler.PrehandlerActionType;
import com.extole.model.entity.prehandler.action.SetSandboxPrehandlerAction;

@Component
public class SetSandboxPrehandlerActionResponseMapper
    implements PrehandlerActionResponseMapper<SetSandboxPrehandlerAction, SetSandboxPrehandlerActionResponse> {

    @Override
    public SetSandboxPrehandlerActionResponse toResponse(SetSandboxPrehandlerAction action) {
        return new SetSandboxPrehandlerActionResponse(action.getId().getValue(), action.getSandbox().getSandboxId());
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.SET_SANDBOX;
    }
}
