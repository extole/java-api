package com.extole.reporting.rest.report.runner;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.reporting.rest.report.execution.ReportFormat;

public class MergingConfigurationResponse {
    private static final String JSON_SORT_BY = "sort_by";
    private static final String JSON_UNIQUE_BY = "unique_by";
    private static final String JSON_FORMATS = "formats";
    private final List<String> sortBy;
    private final List<String> uniqueBy;
    private final Set<ReportFormat> formats;

    public MergingConfigurationResponse(@JsonProperty(JSON_SORT_BY) List<String> sortBy,
        @JsonProperty(JSON_UNIQUE_BY) List<String> uniqueBy,
        @JsonProperty(JSON_FORMATS) Set<ReportFormat> formats) {
        this.sortBy = sortBy;
        this.uniqueBy = uniqueBy;
        this.formats = formats;
    }

    @JsonProperty(JSON_SORT_BY)
    public List<String> getSortBy() {
        return sortBy;
    }

    @JsonProperty(JSON_UNIQUE_BY)
    public List<String> getUniqueBy() {
        return uniqueBy;
    }

    @JsonProperty(JSON_FORMATS)
    public Set<ReportFormat> getFormats() {
        return formats;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<String> sortBy;
        private List<String> uniqueBy;
        private Set<ReportFormat> formats;

        private Builder() {
        }

        public Builder withSortBy(List<String> sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder withUniqueBy(List<String> uniqueBy) {
            this.uniqueBy = uniqueBy;
            return this;
        }

        public Builder withFormats(Set<ReportFormat> formats) {
            this.formats = formats;
            return this;
        }

        public MergingConfigurationResponse build() {
            return new MergingConfigurationResponse(sortBy, uniqueBy, formats);
        }
    }

}
