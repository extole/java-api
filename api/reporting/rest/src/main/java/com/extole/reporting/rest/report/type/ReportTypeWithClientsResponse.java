package com.extole.reporting.rest.report.type;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class ReportTypeWithClientsResponse {
    private static final String JSON_REPORT_TYPE = "report_type";
    private static final String JSON_CLIENT_IDS = "client_ids";

    private final ReportTypeResponse reportType;
    private final Optional<List<String>> clientIds;

    public ReportTypeWithClientsResponse(
        ReportTypeResponse reportType,
        Optional<List<String>> clientIds) {
        this.reportType = reportType;
        this.clientIds = clientIds;
    }

    @JsonProperty(JSON_REPORT_TYPE)
    public ReportTypeResponse getReportType() {
        return reportType;
    }

    @JsonProperty(JSON_CLIENT_IDS)
    public Optional<List<String>> getClientIds() {
        return clientIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static final class Builder {
        private ReportTypeResponse reportType;
        private Optional<List<String>> clientIds = Optional.empty();

        private Builder() {
        }

        public Builder withReportType(ReportTypeResponse reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder withClientIds(List<String> clientIds) {
            this.clientIds = Optional.ofNullable(clientIds);
            return this;
        }

        public ReportTypeWithClientsResponse build() {
            return new ReportTypeWithClientsResponse(reportType, clientIds);
        }
    }
}
