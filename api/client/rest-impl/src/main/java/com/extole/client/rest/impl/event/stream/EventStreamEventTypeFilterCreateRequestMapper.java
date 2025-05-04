package com.extole.client.rest.impl.event.stream;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamEventTypeFilterCreateRequest;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.event.stream.ConsumerEventType;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamEventTypeFilterBuilder;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;

@Component
public class EventStreamEventTypeFilterCreateRequestMapper
    implements EventStreamFilterCreateRequestMapper<EventStreamEventTypeFilterCreateRequest> {

    @Override
    public EventStreamFilter create(EventStreamBuilder builder, EventStreamEventTypeFilterCreateRequest request)
        throws EventStreamFilterRestException {
        EventStreamEventTypeFilterBuilder eventStreamFilterBuilder =
            builder.addFilter(EventStreamFilter.Type.EVENT_TYPE);
        request.getEventTypes()
            .ifPresent(eventTypes -> eventStreamFilterBuilder.withEventTypes(eventTypes.stream()
                .map(eventType -> ConsumerEventType.valueOf(eventType.name())).collect(
                    Collectors.toList())));
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
        return EventFilterType.EVENT_TYPE;
    }
}
