package com.extole.client.rest.impl.prehandler.condition.response;

import org.springframework.stereotype.Component;

import com.extole.client.rest.prehandler.condition.response.EventNameMatchPrehandlerConditionResponse;
import com.extole.model.entity.prehandler.PrehandlerConditionType;
import com.extole.model.entity.prehandler.condition.EventNameMatchPrehandlerCondition;

@Component
public class EventNameMatchPrehandlerConditionResponseMapper implements
    PrehandlerConditionResponseMapper<EventNameMatchPrehandlerCondition, EventNameMatchPrehandlerConditionResponse> {

    @Override
    public EventNameMatchPrehandlerConditionResponse toResponse(EventNameMatchPrehandlerCondition condition) {
        return new EventNameMatchPrehandlerConditionResponse(condition.getId().getValue(), condition.getEventNames());
    }

    @Override
    public PrehandlerConditionType getType() {
        return PrehandlerConditionType.EVENT_NAME_MATCH;
    }
}
