package com.extole.reporting.rest.audience.operation.modification.data.source;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.extole.api.report.Report;
import com.extole.common.lang.ToString;
import com.extole.id.Id;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceResponse;
import com.extole.reporting.rest.audience.operation.AudienceOperationDataSourceType;

public class ReportAudienceOperationDataSourceResponse extends AudienceOperationDataSourceResponse {

    public static final String DATA_SOURCE_TYPE = "REPORT";

    private static final String NAME = "name";
    private static final String EVENT_COLUMNS = "event_columns";
    private static final String EVENT_DATA = "event_data";
    private static final String REPORT_ID = "report_id";

    private final String name;
    private final Set<String> eventColumns;
    private final Map<String, String> eventData;
    private final Id<Report> reportId;

    public ReportAudienceOperationDataSourceResponse(
        @JsonProperty(NAME) String name,
        @JsonProperty(EVENT_COLUMNS) Set<String> eventColumns,
        @JsonProperty(EVENT_DATA) Map<String, String> eventData,
        @JsonProperty(REPORT_ID) Id<Report> reportId) {
        super(AudienceOperationDataSourceType.REPORT);
        this.name = name;
        this.eventColumns = ImmutableSet.copyOf(eventColumns);
        this.eventData = ImmutableMap.copyOf(eventData);
        this.reportId = reportId;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(EVENT_COLUMNS)
    public Set<String> getEventColumns() {
        return eventColumns;
    }

    @JsonProperty(EVENT_DATA)
    public Map<String, String> getEventData() {
        return eventData;
    }

    @JsonProperty(REPORT_ID)
    public Id<Report> getReportId() {
        return reportId;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
