package com.extole.client.rest.impl.prehandler.action.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.action.response.SetDataPrehandlerActionResponse;
import com.extole.model.entity.prehandler.PrehandlerActionType;
import com.extole.model.entity.prehandler.action.SetDataPrehandlerAction;

@Component
public class SetDataPrehandlerActionResponseMapper
    implements PrehandlerActionResponseMapper<SetDataPrehandlerAction, SetDataPrehandlerActionResponse> {

    @Override
    public SetDataPrehandlerActionResponse toResponse(SetDataPrehandlerAction action) {
        return new SetDataPrehandlerActionResponse(action.getId().getValue(), action.getData(),
            action.getDefaultData(), action.getDeleteData());
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.SET_DATA;
    }
}
