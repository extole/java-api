package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamEventNameFilterResponse;
import com.extole.client.rest.event.stream.EventStreamFilterResponse;
import com.extole.model.entity.event.stream.EventStreamEventNameFilter;
import com.extole.model.entity.event.stream.EventStreamFilter;

@Component
public class EventStreamEventNameFilterResponseMapper
    implements EventStreamFilterResponseMapper<EventStreamEventNameFilter> {

    @Override
    public EventStreamFilterResponse map(EventStreamEventNameFilter eventStreamFilter) {
        return new EventStreamEventNameFilterResponse(EventFilterType.valueOf(eventStreamFilter.getType().name()),
            eventStreamFilter.getId(), eventStreamFilter.getEventNames());
    }

    @Override
    public EventStreamFilter.Type getType() {
        return EventStreamFilter.Type.EVENT_NAME;
    }
}
