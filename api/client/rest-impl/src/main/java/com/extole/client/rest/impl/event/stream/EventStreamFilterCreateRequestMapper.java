package com.extole.client.rest.impl.event.stream;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamFilterCreateRequest;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamBuilder;

public interface EventStreamFilterCreateRequestMapper<T extends EventStreamFilterCreateRequest> {

    EventStreamFilter create(EventStreamBuilder builder, T request)
        throws CampaignComponentValidationRestException, InvalidComponentReferenceException,
        EventStreamFilterRestException;

    EventFilterType getType();
}
