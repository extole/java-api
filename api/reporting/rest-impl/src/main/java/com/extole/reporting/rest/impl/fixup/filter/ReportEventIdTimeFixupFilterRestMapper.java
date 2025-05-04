package com.extole.reporting.rest.impl.fixup.filter;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.filter.ReportEventIdTimeFixupFilter;
import com.extole.reporting.rest.fixup.filter.FixupFilterType;
import com.extole.reporting.rest.fixup.filter.ReportEventIdTimeFixupFilterResponse;

@Component
public final class ReportEventIdTimeFixupFilterRestMapper
    implements FixupFilterRestMapper<ReportEventIdTimeFixupFilter, ReportEventIdTimeFixupFilterResponse> {

    @Override
    public ReportEventIdTimeFixupFilterResponse toResponse(ReportEventIdTimeFixupFilter filter) {
        return new ReportEventIdTimeFixupFilterResponse(filter.getId().getValue(),
            FixupFilterType.valueOf(filter.getType().name()),
            filter.getReportId().getValue(), filter.getEventIdAttributeName(), filter.getEventTimeAttributeName());
    }

    @Override
    public com.extole.reporting.entity.fixup.filter.FixupFilterType getType() {
        return com.extole.reporting.entity.fixup.filter.FixupFilterType.REPORT_EVENT_ID_TIME;
    }
}
