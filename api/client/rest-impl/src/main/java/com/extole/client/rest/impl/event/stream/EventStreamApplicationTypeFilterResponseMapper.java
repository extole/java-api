package com.extole.client.rest.impl.event.stream;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamApplicationTypeFilterResponse;
import com.extole.client.rest.event.stream.EventStreamFilterResponse;
import com.extole.model.entity.event.stream.EventStreamApplicationTypeFilter;
import com.extole.model.entity.event.stream.EventStreamFilter;

@Component
public class EventStreamApplicationTypeFilterResponseMapper
    implements EventStreamFilterResponseMapper<EventStreamApplicationTypeFilter> {

    @Override
    public EventStreamFilterResponse map(EventStreamApplicationTypeFilter eventStreamFilter) {
        return new EventStreamApplicationTypeFilterResponse(EventFilterType.valueOf(eventStreamFilter.getType().name()),
            eventStreamFilter.getId(), eventStreamFilter.getApplicationTypes());
    }

    @Override
    public EventStreamFilter.Type getType() {
        return EventStreamFilter.Type.APPLICATION_TYPE;
    }
}
