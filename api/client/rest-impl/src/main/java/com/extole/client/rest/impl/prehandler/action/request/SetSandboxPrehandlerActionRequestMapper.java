package com.extole.client.rest.impl.prehandler.action.request;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.client.rest.prehandler.action.exception.SetSandboxPrehandlerActionRestException;
import com.extole.client.rest.prehandler.action.request.SetSandboxPrehandlerActionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.action.sandbox.InvalidSandboxPrehandlerActionException;
import com.extole.model.service.prehandler.action.sandbox.MissingSandboxPrehandlerActionException;
import com.extole.model.service.prehandler.action.sandbox.SetSandboxPrehandlerActionBuilder;

@Component
public class SetSandboxPrehandlerActionRequestMapper
    implements PrehandlerActionRequestMapper<SetSandboxPrehandlerActionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, SetSandboxPrehandlerActionRequest action)
        throws PrehandlerActionValidationRestException {
        try {
            SetSandboxPrehandlerActionBuilder builder =
                prehandlerBuilder.addAction(com.extole.model.entity.prehandler.PrehandlerActionType.SET_SANDBOX);
            if (!Strings.isNullOrEmpty(action.getSandboxId())) {
                builder.withSandboxId(Id.valueOf(action.getSandboxId()));
            }
            builder.done();
        } catch (InvalidSandboxPrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(SetSandboxPrehandlerActionRestException.class)
                .withErrorCode(SetSandboxPrehandlerActionRestException.PREHANDLER_ACTION_SANDBOX_INVALID)
                .addParameter("sandbox_id", e.getSandboxId().getValue())
                .withCause(e).build();
        } catch (MissingSandboxPrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(SetSandboxPrehandlerActionRestException.class)
                .withErrorCode(SetSandboxPrehandlerActionRestException.PREHANDLER_ACTION_SANDBOX_MISSING)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.SET_SANDBOX;
    }
}
