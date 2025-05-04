package com.extole.reporting.rest.report.type;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportTypeTagResponse {
    private static final String JSON_NAME = "name";
    private static final String JSON_TYPE = "type";

    private final String name;
    private final ReportTypeTagType type;

    @JsonCreator
    public ReportTypeTagResponse(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_TYPE) ReportTypeTagType type) {
        this.name = name;
        this.type = type;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_TYPE)
    public ReportTypeTagType getType() {
        return type;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ReportTypeTagResponse that = (ReportTypeTagResponse) object;
        return Objects.equals(name, that.name) &&
            Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private ReportTypeTagType type;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(ReportTypeTagType type) {
            this.type = type;
            return this;
        }

        public ReportTypeTagResponse build() {
            return new ReportTypeTagResponse(name, type);
        }
    }
}
