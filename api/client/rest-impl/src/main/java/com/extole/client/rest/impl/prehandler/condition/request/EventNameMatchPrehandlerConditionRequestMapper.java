package com.extole.client.rest.impl.prehandler.condition.request;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.PrehandlerConditionValidationRestException;
import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;
import com.extole.client.rest.prehandler.condition.exception.EventNameMatchPrehandlerConditionRestException;
import com.extole.client.rest.prehandler.condition.request.EventNameMatchPrehandlerConditionRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.prehandler.PrehandlerBuilder;
import com.extole.model.service.prehandler.condition.EventNameMatchPrehandlerConditionBuilder;
import com.extole.model.service.prehandler.condition.exception.MissingEventNamePrehandlerConditionException;

@Component
public class EventNameMatchPrehandlerConditionRequestMapper
    implements PrehandlerConditionRequestMapper<EventNameMatchPrehandlerConditionRequest> {

    @Override
    public void update(PrehandlerBuilder prehandlerBuilder, EventNameMatchPrehandlerConditionRequest condition)
        throws PrehandlerConditionValidationRestException {
        try {
            EventNameMatchPrehandlerConditionBuilder builder = prehandlerBuilder
                .addCondition(com.extole.model.entity.prehandler.PrehandlerConditionType.EVENT_NAME_MATCH);
            builder.withEventNames(condition.getEventNames());
            builder.done();
        } catch (MissingEventNamePrehandlerConditionException e) {
            throw RestExceptionBuilder.newBuilder(EventNameMatchPrehandlerConditionRestException.class)
                .withErrorCode(EventNameMatchPrehandlerConditionRestException.PREHANDLER_CONDITION_EVENT_NAME_MISSING)
                .withCause(e).build();
        }
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.EVENT_NAME_MATCH;
    }
}
