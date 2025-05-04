package com.extole.reporting.rest.posthandler.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.reporting.rest.posthandler.ConditionType;

public class MatchReportScheduleReportPostHandlerConditionRequest extends ReportPostHandlerConditionRequest {
    static final String TYPE = "MATCH_REPORT_SCHEDULE";

    private static final String JSON_REPORT_SCHEDULE_ID = "report_schedule_id";

    private final String reportScheduleId;

    @JsonCreator
    public MatchReportScheduleReportPostHandlerConditionRequest(
        @JsonProperty(JSON_REPORT_SCHEDULE_ID) String reportScheduleId) {
        super(ConditionType.MATCH_REPORT_SCHEDULE);
        this.reportScheduleId = reportScheduleId;
    }

    @JsonProperty(JSON_REPORT_SCHEDULE_ID)
    public String getReportScheduleId() {
        return reportScheduleId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String condition;

        private Builder() {
        }

        public Builder withReportScheduleId(String condition) {
            this.condition = condition;
            return this;
        }

        public MatchReportScheduleReportPostHandlerConditionRequest build() {
            return new MatchReportScheduleReportPostHandlerConditionRequest(condition);
        }
    }
}
