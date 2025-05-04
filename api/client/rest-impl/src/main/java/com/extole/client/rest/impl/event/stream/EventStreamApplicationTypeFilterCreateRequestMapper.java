package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamApplicationTypeFilterCreateRequest;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamApplicationTypeFilterBuilder;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;

@Component
public class EventStreamApplicationTypeFilterCreateRequestMapper
    implements EventStreamFilterCreateRequestMapper<EventStreamApplicationTypeFilterCreateRequest> {

    @Override
    public EventStreamFilter create(EventStreamBuilder builder, EventStreamApplicationTypeFilterCreateRequest request)
        throws EventStreamFilterRestException {
        EventStreamApplicationTypeFilterBuilder eventStreamFilterBuilder =
            builder.addFilter(EventStreamFilter.Type.APPLICATION_TYPE);
        request.getApplicationTypes()
            .ifPresent(applicationTypes -> eventStreamFilterBuilder.withApplicationTypes(applicationTypes));
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
        return EventFilterType.APPLICATION_TYPE;
    }
}
