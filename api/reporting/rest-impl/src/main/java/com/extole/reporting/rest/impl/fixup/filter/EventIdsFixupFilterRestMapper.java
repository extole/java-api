package com.extole.reporting.rest.impl.fixup.filter;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.id.Id;
import com.extole.reporting.entity.fixup.filter.EventIdsFixupFilter;
import com.extole.reporting.rest.fixup.filter.EventIdsFixupFilterResponse;
import com.extole.reporting.rest.fixup.filter.FixupFilterType;

@Component
public class EventIdsFixupFilterRestMapper
    implements FixupFilterRestMapper<EventIdsFixupFilter, EventIdsFixupFilterResponse> {

    @Override
    public EventIdsFixupFilterResponse toResponse(EventIdsFixupFilter filter) {
        return new EventIdsFixupFilterResponse(filter.getId().getValue(),
            FixupFilterType.valueOf(filter.getType().name()),
            filter.getEventIds().stream().map(Id::getValue).collect(Collectors.toSet()));
    }

    @Override
    public com.extole.reporting.entity.fixup.filter.FixupFilterType getType() {
        return com.extole.reporting.entity.fixup.filter.FixupFilterType.EVENT_IDS;
    }
}
