package com.extole.client.rest.impl.event.stream;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.ConsumerEventType;
import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamEventTypeFilterResponse;
import com.extole.client.rest.event.stream.EventStreamFilterResponse;
import com.extole.model.entity.event.stream.EventStreamEventTypeFilter;
import com.extole.model.entity.event.stream.EventStreamFilter;

@Component
public class EventStreamEventTypeFilterResponseMapper
    implements EventStreamFilterResponseMapper<EventStreamEventTypeFilter> {

    @Override
    public EventStreamFilterResponse map(EventStreamEventTypeFilter eventStreamFilter) {
        return new EventStreamEventTypeFilterResponse(EventFilterType.valueOf(eventStreamFilter.getType().name()),
            eventStreamFilter.getId(), eventStreamFilter.getEventTypes().stream()
                .map(eventType -> ConsumerEventType.valueOf(eventType.name()))
                .collect(Collectors.toList()));
    }

    @Override
    public EventStreamFilter.Type getType() {
        return EventStreamFilter.Type.EVENT_TYPE;
    }
}
