package com.extole.reporting.rest.audience.list.request;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.audience.list.AudienceListType;

public class DynamicAudienceListRequest extends AudienceListRequest {
    static final String AUDIENCE_TYPE = "DYNAMIC";

    private static final String REPORT_RUNNER_ID = "report_runner_id";

    private final String reportRunnerId;

    public DynamicAudienceListRequest(
        @Parameter(description = "AudienceList name, max length 255")
        @JsonProperty(NAME) Omissible<String> name,
        @Parameter(description = "AudienceList description, max length 1024")
        @JsonProperty(DESCRIPTION) Omissible<String> description,
        @Parameter(description = "A list of columns that will be used when dispatching AudienceList")
        @JsonProperty(EVENT_COLUMNS) Omissible<Set<String>> eventColumns,
        @Parameter(description = "Data for the AudienceList")
        @JsonProperty(EVENT_DATA) Omissible<Map<String, String>> eventData,
        @Parameter(description = "A set of tags for the AudienceList")
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @Parameter(description = "An existing reportRunnerId for the AudienceList")
        @JsonProperty(REPORT_RUNNER_ID) String reportRunnerId) {
        super(AudienceListType.DYNAMIC, name, description, eventColumns, eventData, tags);
        this.reportRunnerId = reportRunnerId;
    }

    @JsonProperty(REPORT_RUNNER_ID)
    public String getReportRunnerId() {
        return reportRunnerId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String reportRunnerId;
        private Omissible<String> name = Omissible.omitted();
        private Omissible<String> description = Omissible.omitted();
        private Omissible<Set<String>> eventColumns = Omissible.omitted();
        private Omissible<Map<String, String>> eventData = Omissible.omitted();
        private Omissible<Set<String>> tags = Omissible.omitted();

        private Builder() {
        }

        public Builder withReportRunnerId(String reportId) {
            this.reportRunnerId = reportId;
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

        public Builder withEventData(Map<String, String> eventData) {
            this.eventData = Omissible.of(eventData);
            return this;
        }

        public Builder withTags(Set<String> tags) {
            this.tags = Omissible.of(tags);
            return this;
        }

        public DynamicAudienceListRequest build() {
            return new DynamicAudienceListRequest(name, description, eventColumns, eventData, tags, reportRunnerId);
        }
    }
}
