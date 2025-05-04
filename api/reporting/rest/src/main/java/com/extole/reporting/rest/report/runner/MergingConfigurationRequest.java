package com.extole.reporting.rest.report.runner;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.reporting.rest.report.execution.ReportFormat;

public class MergingConfigurationRequest {
    private static final String JSON_SORT_BY = "sort_by";
    private static final String JSON_UNIQUE_BY = "unique_by";
    private static final String JSON_FORMATS = "formats";
    private final Omissible<List<String>> sortBy;
    private final Omissible<List<String>> uniqueBy;
    private final Omissible<Set<ReportFormat>> formats;

    public MergingConfigurationRequest(@JsonProperty(JSON_SORT_BY) Omissible<List<String>> sortBy,
        @JsonProperty(JSON_UNIQUE_BY) Omissible<List<String>> uniqueBy,
        @JsonProperty(JSON_FORMATS) Omissible<Set<ReportFormat>> formats) {
        this.sortBy = sortBy;
        this.uniqueBy = uniqueBy;
        this.formats = formats;
    }

    @JsonProperty(JSON_SORT_BY)
    public Omissible<List<String>> getSortBy() {
        return sortBy;
    }

    @JsonProperty(JSON_UNIQUE_BY)
    public Omissible<List<String>> getUniqueBy() {
        return uniqueBy;
    }

    @JsonProperty(JSON_FORMATS)
    public Omissible<Set<ReportFormat>> getFormats() {
        return formats;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Omissible<List<String>> sortBy = Omissible.omitted();
        private Omissible<List<String>> uniqueBy = Omissible.omitted();
        private Omissible<Set<ReportFormat>> formats = Omissible.omitted();

        private Builder() {
        }

        public Builder withSortBy(List<String> sortBy) {
            this.sortBy = Omissible.of(sortBy);
            return this;
        }

        public Builder withUniqueBy(List<String> uniqueBy) {
            this.uniqueBy = Omissible.of(uniqueBy);
            return this;
        }

        public Builder withFormats(Set<ReportFormat> formats) {
            this.formats = Omissible.of(formats);
            return this;
        }

        public MergingConfigurationRequest build() {
            return new MergingConfigurationRequest(sortBy, uniqueBy, formats);
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
