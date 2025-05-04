package com.extole.reporting.rest.impl.fixup.filter;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.fixup.filter.ReportIdFixupFilter;
import com.extole.reporting.rest.fixup.filter.FixupFilterType;
import com.extole.reporting.rest.fixup.filter.ReportEventIdFixupFilterResponse;

@Component
public class ReportEventIdFixupFilterRestMapper
    implements FixupFilterRestMapper<ReportIdFixupFilter, ReportEventIdFixupFilterResponse> {

    @Override
    public ReportEventIdFixupFilterResponse toResponse(ReportIdFixupFilter filter) {
        return new ReportEventIdFixupFilterResponse(filter.getId().getValue(),
            FixupFilterType.valueOf(filter.getType().name()),
            filter.getReportId().getValue(), filter.getEventIdAttributeName());
    }

    @Override
    public com.extole.reporting.entity.fixup.filter.FixupFilterType getType() {
        return com.extole.reporting.entity.fixup.filter.FixupFilterType.REPORT_ID;
    }
}
