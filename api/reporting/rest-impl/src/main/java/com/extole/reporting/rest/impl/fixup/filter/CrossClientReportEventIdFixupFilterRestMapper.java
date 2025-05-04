package com.extole.reporting.rest.impl.fixup.filter;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.filter.CrossClientReportIdFixupFilter;
import com.extole.reporting.rest.fixup.filter.CrossClientReportEventIdFixupFilterResponse;
import com.extole.reporting.rest.fixup.filter.FixupFilterType;

@Component
public class CrossClientReportEventIdFixupFilterRestMapper
    implements FixupFilterRestMapper<CrossClientReportIdFixupFilter, CrossClientReportEventIdFixupFilterResponse> {

    @Override
    public CrossClientReportEventIdFixupFilterResponse toResponse(CrossClientReportIdFixupFilter filter) {
        return new CrossClientReportEventIdFixupFilterResponse(filter.getId().getValue(),
            FixupFilterType.valueOf(filter.getType().name()),
            filter.getReportId().getValue(), filter.getEventIdAttributeName(), filter.getClientIdAttributeName());
    }

    @Override
    public com.extole.reporting.entity.fixup.filter.FixupFilterType getType() {
        return com.extole.reporting.entity.fixup.filter.FixupFilterType.CROSS_CLIENT_REPORT_ID;
    }
}
