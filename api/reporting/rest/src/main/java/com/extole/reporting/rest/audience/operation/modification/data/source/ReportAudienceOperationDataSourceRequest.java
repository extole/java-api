package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.report.Report;
import com.extole.common.rest.omissible.Omissible;
import com.extole.id.Id;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceRequest;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class ReportAudienceOperationDataSourceRequest extends AudienceOperationDataSourceRequest {

    public static final String DATA_SOURCE_TYPE = "REPORT";

    private static final String EVENT_COLUMNS = "event_columns";
    private static final String EVENT_DATA = "event_data";
    private static final String REPORT_ID = "report_id";

    private final Omissible<Set<String>> eventColumns;
    private final Omissible<Map<String, String>> eventData;
    private final Id<Report> reportId;

    public ReportAudienceOperationDataSourceRequest(@JsonProperty(EVENT_COLUMNS) Omissible<Set<String>> eventColumns,
        @JsonProperty(EVENT_DATA) Omissible<Map<String, String>> eventData,
        @JsonProperty(REPORT_ID) Id<Report> reportId) {
        super(AudienceOperationDataSourceType.REPORT);
        this.eventColumns = eventColumns;
        this.eventData = eventData;
        this.reportId = reportId;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Omissible<Set<String>> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Omissible<Map<String, String>> getEventData() {
        return eventData;
    }

    @JsonProperty(REPORT_ID)
    public Id<Report> getReportId() {
        return reportId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<Set<String>> eventColumns = Omissible.omitted();
        private Omissible<Map<String, String>> eventData = Omissible.omitted();
        private Id<Report> reportId;

        private Builder() {

        }

        public Builder withEventColumns(Set<String> eventColumns) {
            this.eventColumns = Omissible.of(eventColumns);
            return this;
        }

        public Builder withEventData(Map<String, String> eventData) {
            this.eventData = Omissible.of(eventData);
            return this;
        }

        public Builder withReportId(Id<Report> reportId) {
            this.reportId = reportId;
            return this;
        }

        public ReportAudienceOperationDataSourceRequest build() {
            return new ReportAudienceOperationDataSourceRequest(eventColumns, eventData, reportId);
        }

    }

}
