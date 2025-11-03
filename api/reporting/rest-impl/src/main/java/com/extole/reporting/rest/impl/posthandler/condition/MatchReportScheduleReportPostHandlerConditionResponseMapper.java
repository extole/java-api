package com.extole.reporting.rest.impl.posthandler.condition;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.posthandler.condition.MatchReportScheduleReportPostHandlerCondition;
import com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition;
import com.extole.reporting.rest.posthandler.condition.MatchReportScheduleReportPostHandlerConditionResponse;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionResponse;

@Component
public class MatchReportScheduleReportPostHandlerConditionResponseMapper
    implements
    ReportPostHandlerConditionResponseMapper<MatchReportScheduleReportPostHandlerCondition,
        ReportPostHandlerConditionResponse> {

    @Override
    public ReportPostHandlerConditionResponse toReponse(MatchReportScheduleReportPostHandlerCondition condition) {
        return new MatchReportScheduleReportPostHandlerConditionResponse(condition.getId().getValue(),
            condition.getReportScheduleId() != null ? condition.getReportScheduleId().getValue() : null);
    }

    @Override
    public ReportPostHandlerCondition.ConditionType getType() {
        return ReportPostHandlerCondition.ConditionType.MATCH_REPORT_SCHEDULE;
    }
}
