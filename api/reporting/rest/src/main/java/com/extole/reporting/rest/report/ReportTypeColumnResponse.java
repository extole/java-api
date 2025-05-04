package com.extole.reporting.rest.report;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportTypeColumnResponse {
    private static final String JSON_NAME = "name";
    private static final String JSON_SAMPLE_VALUE = "sample_value";
    private static final String JSON_NOTE = "note";

    private final String name;
    private final String sampleValue;
    private final String note;

    public ReportTypeColumnResponse(
        @JsonProperty(JSON_NAME) String name,
        @Nullable @JsonProperty(JSON_SAMPLE_VALUE) String sampleValue,
        @Nullable @JsonProperty(JSON_NOTE) String note) {
        this.name = name;
        this.sampleValue = sampleValue;
        this.note = note;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @Nullable
    @JsonProperty(JSON_SAMPLE_VALUE)
    public String getSampleValue() {
        return sampleValue;
    }

    @Nullable
    @JsonProperty(JSON_NOTE)
    public String getNote() {
        return note;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String sampleValue;
        private String note;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withSampleValue(String sampleValue) {
            this.sampleValue = sampleValue;
            return this;
        }

        public Builder withNote(String note) {
            this.note = note;
            return this;
        }

        public ReportTypeColumnResponse build() {
            return new ReportTypeColumnResponse(name, sampleValue, note);
        }
    }
}
