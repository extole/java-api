package com.extole.client.rest.impl.event.stream;

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.client.rest.event.stream.EventStreamFilterUpdateRequest;
import com.extole.id.Id;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamFilterNotFoundException;
import com.extole.model.service.event.stream.EventStreamNotFoundException;

public interface EventStreamFilterUpdateRequestMapper<T extends EventStreamFilterUpdateRequest> {

    EventStreamFilter update(EventStreamBuilder builder, Id<EventStreamFilter> filterId, T request)
        throws CampaignComponentValidationRestException, EventStreamNotFoundException,
        EventStreamFilterNotFoundException, InvalidComponentReferenceException, EventStreamFilterRestException;

    EventFilterType getType();
}
