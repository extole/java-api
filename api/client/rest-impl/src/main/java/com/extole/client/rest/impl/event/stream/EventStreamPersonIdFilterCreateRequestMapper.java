package com.extole.client.rest.impl.event.stream;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.client.rest.event.stream.EventStreamPersonIdFilterCreateRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;
import com.extole.model.service.event.stream.EventStreamPersonIdFilterBuilder;
import com.extole.person.service.profile.PersonHandle;

@Component
public class EventStreamPersonIdFilterCreateRequestMapper
    implements EventStreamFilterCreateRequestMapper<EventStreamPersonIdFilterCreateRequest> {

    @Override
    public EventStreamFilter create(EventStreamBuilder builder, EventStreamPersonIdFilterCreateRequest request)
        throws EventStreamFilterRestException {
        EventStreamPersonIdFilterBuilder eventStreamFilterBuilder =
            builder.addFilter(EventStreamFilter.Type.PERSON_ID);
        List<Id<PersonHandle>> personIds = Collections.emptyList();
        if (request.getPersonIds().isPresent()) {
            personIds = request.getPersonIds().getValue()
                .stream()
                .map(item -> Id.<PersonHandle>valueOf(item.getValue()))
                .collect(Collectors.toList());
        }
        eventStreamFilterBuilder.withPersonIds(personIds);
        try {
            return eventStreamFilterBuilder.done();
        } catch (EventStreamFiltersValidationException e) {
            throw RestExceptionBuilder.newBuilder(EventStreamFilterRestException.class)
                .withErrorCode(EventStreamFilterRestException.EVENT_STREAM_FILTER_VALIDATION_EXCEPTION)
                .addParameter("event_stream_id", e.getEventStreamId())
                .addParameter("filter_id", e.getEventStreamFilterId())
                .addParameter("message", e.getMessage())
                .build();
        }
    }

    @Override
    public EventFilterType getType() {
        return EventFilterType.PERSON_ID;
    }
}
