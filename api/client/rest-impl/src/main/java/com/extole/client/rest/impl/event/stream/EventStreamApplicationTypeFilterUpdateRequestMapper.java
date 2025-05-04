package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamApplicationTypeFilterUpdateRequest;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamApplicationTypeFilterBuilder;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamFilterNotFoundException;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;
import com.extole.model.service.event.stream.EventStreamNotFoundException;

@Component
public class EventStreamApplicationTypeFilterUpdateRequestMapper
    implements EventStreamFilterUpdateRequestMapper<EventStreamApplicationTypeFilterUpdateRequest> {

    @Override
    public EventStreamFilter update(EventStreamBuilder builder, Id<EventStreamFilter> filterId,
        EventStreamApplicationTypeFilterUpdateRequest request)
        throws EventStreamNotFoundException, EventStreamFilterNotFoundException, EventStreamFilterRestException {
        EventStreamApplicationTypeFilterBuilder eventStreamFilterBuilder =
            builder.updateFilter(filterId);
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
