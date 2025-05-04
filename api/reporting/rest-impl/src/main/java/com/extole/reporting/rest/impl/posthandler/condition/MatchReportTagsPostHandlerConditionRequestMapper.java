package com.extole.reporting.rest.impl.posthandler.condition;

import static com.extole.reporting.entity.report.posthandler.condition.ReportPostHandlerCondition.ConditionType.MATCH_REPORT_TAGS;

import org.springframework.stereotype.Component;

import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.reporting.rest.posthandler.ConditionType;
import com.extole.reporting.rest.posthandler.ReportPostHandlerConditionValidationRestException;
import com.extole.reporting.rest.posthandler.condition.MatchReportTagsPostHandlerConditionRequest;
import com.extole.reporting.rest.posthandler.condition.exception.MatchReportTagsPostHandlerConditionValidationRestException;
import com.extole.reporting.service.posthandler.ReportPostHandlerBuilder;
import com.extole.reporting.service.posthandler.ReportPostHandlerConditionBuildException;
import com.extole.reporting.service.posthandler.condition.InvalidTagReportPostHandlerConditionBuildException;
import com.extole.reporting.service.posthandler.condition.MatchReportTagsPostHandlerConditionBuilder;
import com.extole.reporting.service.posthandler.condition.MissingTagsReportPostHandlerConditionBuildException;

@Component
public class MatchReportTagsPostHandlerConditionRequestMapper
    implements ReportPostHandlerConditionRequestMapper<MatchReportTagsPostHandlerConditionRequest> {

    @Override
    public void upload(ReportPostHandlerBuilder builder, MatchReportTagsPostHandlerConditionRequest request)
        throws ReportPostHandlerConditionValidationRestException {
        MatchReportTagsPostHandlerConditionBuilder conditionBuilder = builder.addCondition(MATCH_REPORT_TAGS);

        try {
            conditionBuilder
                .withReportTags(request.getReportTags())
                .done();
        } catch (MissingTagsReportPostHandlerConditionBuildException e) {
            throw RestExceptionBuilder
                .newBuilder(MatchReportTagsPostHandlerConditionValidationRestException.class)
                .withErrorCode(MatchReportTagsPostHandlerConditionValidationRestException.MISSING_TAGS)
                .withCause(e)
                .build();
        } catch (InvalidTagReportPostHandlerConditionBuildException e) {
            throw RestExceptionBuilder
                .newBuilder(MatchReportTagsPostHandlerConditionValidationRestException.class)
                .withErrorCode(MatchReportTagsPostHandlerConditionValidationRestException.INVALID_TAG)
                .addParameter("tag", e.getReportTag())
                .withCause(e)
                .build();
        } catch (ReportPostHandlerConditionBuildException e) {
            throw RestExceptionBuilder
                .newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ConditionType getType() {
        return ConditionType.MATCH_REPORT_TAGS;
    }
}
