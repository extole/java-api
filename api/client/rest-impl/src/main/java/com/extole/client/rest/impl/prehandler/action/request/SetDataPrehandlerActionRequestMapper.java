package com.extole.client.rest.impl.prehandler.action.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerActionValidationRestException;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.client.rest.prehandler.action.exception.SetDataPrehandlerActionRestException;
import com.extole.client.rest.prehandler.action.request.SetDataPrehandlerActionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.action.data.InvalidDataNamePrehandlerActionException;
import com.extole.model.service.prehandler.action.data.InvalidDataValuePrehandlerActionException;
import com.extole.model.service.prehandler.action.data.MissingDataPrehandlerActionException;
import com.extole.model.service.prehandler.action.data.SetDataPrehandlerActionBuilder;

@Component
public class SetDataPrehandlerActionRequestMapper
    implements PrehandlerActionRequestMapper<SetDataPrehandlerActionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, SetDataPrehandlerActionRequest action)
        throws PrehandlerActionValidationRestException {
        try {
            SetDataPrehandlerActionBuilder builder =
                prehandlerBuilder.addAction(com.extole.model.entity.prehandler.PrehandlerActionType.SET_DATA);
            builder.withData(action.getData());
            builder.withDefaultData(action.getDefaultData());
            builder.withDeleteData(action.getDeleteData());
            builder.done();
        } catch (MissingDataPrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(SetDataPrehandlerActionRestException.class)
                .withErrorCode(SetDataPrehandlerActionRestException.PREHANDLER_ACTION_DATA_MISSING)
                .withCause(e).build();
        } catch (InvalidDataNamePrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(SetDataPrehandlerActionRestException.class)
                .withErrorCode(SetDataPrehandlerActionRestException.PREHANDLER_ACTION_DATA_NAME_INVALID)
                .addParameter("name", e.getName())
                .withCause(e).build();
        } catch (InvalidDataValuePrehandlerActionException e) {
            throw RestExceptionBuilder.newBuilder(SetDataPrehandlerActionRestException.class)
                .withErrorCode(SetDataPrehandlerActionRestException.PREHANDLER_ACTION_DATA_VALUE_INVALID)
                .addParameter("name", e.getName())
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerActionType getType() {
        return PrehandlerActionType.SET_DATA;
    }
}
