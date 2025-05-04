package com.extole.reporting.rest.impl.posthandler.condition;

import org.springframework.stereotype.Component;

import com.extole.reporting.entity.report.posthandler.condition.MatchReportTagsPostHandlerCondition;
import com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition;
import com.extole.reporting.rest.posthandler.condition.MatchReportTagsPostHandlerConditionResponse;
import com.extole.reporting.rest.posthandler.condition.ReportPostHandlerConditionResponse;

@Component
public class MatchReportTagsPostHandlerConditionResponseMapper
    implements
    ReportPostHandlerConditionResponseMapper<MatchReportTagsPostHandlerCondition, ReportPostHandlerConditionResponse> {

    @Override
    public ReportPostHandlerConditionResponse toReponse(MatchReportTagsPostHandlerCondition condition) {
        return new MatchReportTagsPostHandlerConditionResponse(condition.getId().getValue(),
            condition.getReportTags());
    }

    @Override
    public ReportPostHandlerCondition.ConditionType getType() {
        return ReportPostHandlerCondition.ConditionType.MATCH_REPORT_TAGS;
    }
}
