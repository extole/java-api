package com.extole.reporting.rest.impl.posthandler.condition;

import static com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition.ConditionType.MATCH_REPORT_SCHEDULE;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.id.Id;
import com.extole.reporting.rest.posthandler.ConditionType;
import com.extole.reporting.rest.posthandler.ReportPostHandlerConditionValidationRestException;
import com.extole.reporting.rest.posthandler.condition.MatchReportScheduleReportPostHandlerConditionRequest;
import com.extole.reporting.rest.posthandler.condition.exception.MatchReportScheduleReportPostHandlerConditionValidationRestException;
import com.extole.reporting.service.posthandler.MissingMatchReportScheduleConditionException;
import com.extole.reporting.service.posthandler.ReportPostHandlerBuilder;
import com.extole.reporting.service.posthandler.ScheduleReportNotFoundReportScheduleConditionException;
import com.extole.reporting.service.posthandler.condition.MatchReportScheduleReportPostHandlerConditionBuilder;

@Component
public class MatchReportScheduleReportPostHandlerConditionRequestMapper
    implements ReportPostHandlerConditionRequestMapper<MatchReportScheduleReportPostHandlerConditionRequest> {

    @Override
    public void upload(ReportPostHandlerBuilder builder, MatchReportScheduleReportPostHandlerConditionRequest request)
        throws ReportPostHandlerConditionValidationRestException {
        MatchReportScheduleReportPostHandlerConditionBuilder conditionBuilder =
            builder.addCondition(MATCH_REPORT_SCHEDULE);

        try {
            if (!Strings.isNullOrEmpty(request.getReportScheduleId())) {
                conditionBuilder.withReportSchedule(Id.valueOf(request.getReportScheduleId()));
            }

            conditionBuilder.done();
        } catch (MissingMatchReportScheduleConditionException e) {
            throw RestExceptionBuilder
                .newBuilder(MatchReportScheduleReportPostHandlerConditionValidationRestException.class)
                .withErrorCode(
                    MatchReportScheduleReportPostHandlerConditionValidationRestException.REPORT_SCHEDULE_ID_MISSING)
                .withCause(e)
                .build();
        } catch (ScheduleReportNotFoundReportScheduleConditionException e) {
            throw RestExceptionBuilder
                .newBuilder(MatchReportScheduleReportPostHandlerConditionValidationRestException.class)
                .withErrorCode(
                    MatchReportScheduleReportPostHandlerConditionValidationRestException.REPORT_SCHEDULE_NOT_FOUND)
                .addParameter("id", e.getReportScheduleId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public ConditionType getType() {
        return ConditionType.MATCH_REPORT_SCHEDULE;
    }
}
