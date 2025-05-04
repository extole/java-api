package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamFilterResponse;
import com.extole.client.rest.event.stream.EventStreamSandboxFilterResponse;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.entity.event.stream.EventStreamSandboxFilter;

@Component
public class EventStreamSandboxFilterResponseMapper
    implements EventStreamFilterResponseMapper<EventStreamSandboxFilter> {

    @Override
    public EventStreamFilterResponse map(EventStreamSandboxFilter eventStreamFilter) {
        return new EventStreamSandboxFilterResponse(EventFilterType.valueOf(eventStreamFilter.getType().name()),
            eventStreamFilter.getId(), eventStreamFilter.getSandboxes(), eventStreamFilter.getContainers());
    }

    @Override
    public EventStreamFilter.Type getType() {
        return EventStreamFilter.Type.SANDBOX;
    }
}
