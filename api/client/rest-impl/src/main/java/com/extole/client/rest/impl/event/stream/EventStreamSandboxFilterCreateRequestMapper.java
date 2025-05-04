package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamFilterRestException;
import com.extole.client.rest.event.stream.EventStreamSandboxFilterCreateRequest;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.service.event.stream.EventStreamBuilder;
import com.extole.model.service.event.stream.EventStreamFiltersValidationException;
import com.extole.model.service.event.stream.EventStreamSandboxFilterBuilder;

@Component
public class EventStreamSandboxFilterCreateRequestMapper
    implements EventStreamFilterCreateRequestMapper<EventStreamSandboxFilterCreateRequest> {

    @Override
    public EventStreamFilter create(EventStreamBuilder builder, EventStreamSandboxFilterCreateRequest request)
        throws EventStreamFilterRestException {
        EventStreamSandboxFilterBuilder eventStreamFilterBuilder =
            builder.addFilter(EventStreamFilter.Type.SANDBOX);
        request.getSandboxes()
            .ifPresent(sandboxes -> eventStreamFilterBuilder.withSandboxes(sandboxes));
        request.getContainers()
            .ifPresent(containers -> eventStreamFilterBuilder.withContainers(containers));
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
        return EventFilterType.SANDBOX;
    }
}
