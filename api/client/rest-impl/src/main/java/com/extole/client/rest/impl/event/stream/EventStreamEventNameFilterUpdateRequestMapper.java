package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamEventNameFilterUpdateRequest;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamEventNameFilterBuilder;
import com.extole.model.service.event.stream.EventStreamFilterNotFoundException;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;
import com.extole.model.service.event.stream.EventStreamNotFoundException;

@Component
public class EventStreamEventNameFilterUpdateRequestMapper
    implements EventStreamFilterUpdateRequestMapper<EventStreamEventNameFilterUpdateRequest> {

    @Override
    public EventStreamFilter update(EventStreamBuilder builder, Id<EventStreamFilter> filterId,
        EventStreamEventNameFilterUpdateRequest request)
        throws EventStreamNotFoundException, EventStreamFilterNotFoundException, EventStreamFilterRestException {
        EventStreamEventNameFilterBuilder eventStreamFilterBuilder =
            builder.updateFilter(filterId);
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
