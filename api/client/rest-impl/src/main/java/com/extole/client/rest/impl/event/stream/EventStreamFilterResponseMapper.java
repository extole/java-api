package com.extole.client.rest.impl.event.stream;

import com.extole.client.rest.event.stream.EventStreamFilterResponse;
import com.extole.model.entity.event.stream.EventStreamFilter;

public interface EventStreamFilterResponseMapper<T extends EventStreamFilter> {

    <R extends EventStreamFilterResponse> R map(T eventStreamFilter);

    EventStreamFilter.Type getType();
}
