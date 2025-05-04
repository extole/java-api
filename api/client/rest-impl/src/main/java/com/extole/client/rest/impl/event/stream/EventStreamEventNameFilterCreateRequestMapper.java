package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamEventNameFilterCreateRequest;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamEventNameFilterBuilder;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;

@Component
public class EventStreamEventNameFilterCreateRequestMapper
    implements EventStreamFilterCreateRequestMapper<EventStreamEventNameFilterCreateRequest> {

    @Override
    public EventStreamFilter create(EventStreamBuilder builder, EventStreamEventNameFilterCreateRequest request)
        throws EventStreamFilterRestException {
        EventStreamEventNameFilterBuilder eventStreamFilterBuilder =
            builder.addFilter(EventStreamFilter.Type.EVENT_NAME);
        request.getEventNames()
            .ifPresent(eventNames -> eventStreamFilterBuilder.withEventNames(eventNames));
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
        return EventFilterType.EVENT_NAME;
    }
}
