package com.extole.client.rest.impl.event.stream;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.event.stream.EventFilterType;
import com.extole.client.rest.event.stream.EventStreamFilterResponse;
import com.extole.client.rest.event.stream.EventStreamPersonIdFilterResponse;
import com.extole.id.Id;
import com.extole.model.entity.event.stream.EventStreamFilter;
import com.extole.model.entity.event.stream.EventStreamPersonIdFilter;

@Component
public class EventStreamPersonIdFilterResponseMapper
    implements EventStreamFilterResponseMapper<EventStreamPersonIdFilter> {

    @Override
    public EventStreamFilterResponse map(EventStreamPersonIdFilter eventStreamFilter) {
        return new EventStreamPersonIdFilterResponse(EventFilterType.valueOf(eventStreamFilter.getType().name()),
            eventStreamFilter.getId(), eventStreamFilter.getPersonIds().stream()
                .map(item -> Id.valueOf(item.getValue())).collect(Collectors.toList()));
    }

    @Override
    public EventStreamFilter.Type getType() {
        return EventStreamFilter.Type.PERSON_ID;
    }
}
