package com.extole.reporting.rest.posthandler.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.posthandler.ConditionType;

public class MatchReportScheduleReportPostHandlerConditionResponse extends ReportPostHandlerConditionResponse {
    static final String TYPE = "MATCH_REPORT_SCHEDULE";

    private static final String JSON_REPORT_SCHEDULE_ID = "report_schedule_id";

    private final String reportScheduleId;

    @JsonCreator
    public MatchReportScheduleReportPostHandlerConditionResponse(@JsonProperty(JSON_ID) String conditionId,
        @JsonProperty(JSON_REPORT_SCHEDULE_ID) String reportScheduleId) {
        super(conditionId, ConditionType.MATCH_REPORT_SCHEDULE);
        this.reportScheduleId = reportScheduleId;
    }

    @JsonProperty(JSON_REPORT_SCHEDULE_ID)
    public String getReportScheduleId() {
        return reportScheduleId;
    }

}
