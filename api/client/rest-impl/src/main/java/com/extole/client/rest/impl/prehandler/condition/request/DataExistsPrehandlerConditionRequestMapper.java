package com.extole.client.rest.impl.prehandler.condition.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.client.rest.prehandler.condition.exception.DataExistsPrehandlerConditionRestException;
import com.extole.client.rest.prehandler.condition.request.DataExistsPrehandlerConditionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.condition.DataExistsPrehandlerConditionBuilder;
import com.extole.model.service.prehandler.condition.exception.InvalidDataKeyPrehandlerConditionException;
import com.extole.model.service.prehandler.condition.exception.MissingDataPrehandlerConditionException;

@Component
public class DataExistsPrehandlerConditionRequestMapper
    implements PrehandlerConditionRequestMapper<DataExistsPrehandlerConditionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, DataExistsPrehandlerConditionRequest condition)
        throws PrehandlerConditionValidationRestException {
        try {
            DataExistsPrehandlerConditionBuilder builder = prehandlerBuilder
                .addCondition(com.extole.model.entity.prehandler.PrehandlerConditionType.DATA_EXISTS);
            builder.withDataKeys(condition.getDataKeys());
            builder.done();
        } catch (MissingDataPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(DataExistsPrehandlerConditionRestException.class)
                .withErrorCode(DataExistsPrehandlerConditionRestException.PREHANDLER_CONDITION_DATA_IS_MISSING)
                .withCause(e).build();
        } catch (InvalidDataKeyPrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(DataExistsPrehandlerConditionRestException.class)
                .withErrorCode(DataExistsPrehandlerConditionRestException.PREHANDLER_CONDITION_DATA_INVALID)
                .addParameter("params", e.getKeys())
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.DATA_EXISTS;
    }
}
