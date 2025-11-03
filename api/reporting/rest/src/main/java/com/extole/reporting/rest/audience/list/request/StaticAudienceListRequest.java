package com.extole.reporting.rest.audience.list.request;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.audience.list.AudienceListType;

public class StaticAudienceListRequest extends AudienceListRequest {
    static final String AUDIENCE_TYPE = "STATIC";

    private static final String REPORT_ID = "report_id";

    private final String reportId;

    public StaticAudienceListRequest(
        @Parameter(description = "AudienceList name, max length 255") @JsonProperty(NAME) Omissible<String> name,
        @Parameter(
            description = "AudienceList description, max length 1024") @JsonProperty(DESCRIPTION) Omissible<
                String> description,
        @Parameter(
            description = "A list of columns that will be used when dispatching AudienceList") @JsonProperty(EVENT_COLUMNS) Omissible<
                Set<String>> eventColumns,
        @Parameter(
            description = "Data for the AudienceList") @JsonProperty(EVENT_DATA) Omissible<
                Map<String, String>> eventData,
        @Parameter(description = "A set of tags for the AudienceList") @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @Parameter(
            description = "An existing reportId for the AudienceList") @JsonProperty(REPORT_ID) String reportId) {
        super(AudienceListType.STATIC, name, description, eventColumns, eventData, tags);
        this.reportId = reportId;
    }

    @JsonProperty(REPORT_ID)
    public String getReportId() {
        return reportId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String reportId;
        private Omissible<String> name = Omissible.omitted();
        private Omissible<String> description = Omissible.omitted();
        private Omissible<Set<String>> eventColumns = Omissible.omitted();
        private Omissible<Map<String, String>> eventData = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withReportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public Builder withName(String name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withEventColumns(Set<String> eventColumns) {
            this.eventColumns = Omissible.of(eventColumns);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public Builder withEventData(Map<String, String> eventData) {
            this.eventData = Omissible.of(eventData);
            return this;
        }

        public StaticAudienceListRequest build() {
            return new StaticAudienceListRequest(name, description, eventColumns, eventData, tags, reportId);
        }
    }
}
