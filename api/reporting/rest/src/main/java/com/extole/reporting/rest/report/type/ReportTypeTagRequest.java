package com.extole.reporting.rest.report.type;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportTypeTagRequest {
    private static final String JSON_NAME = "name";
    private static final String JSON_TYPE = "type";

    private final String name;
    private final Optional<ReportTypeTagType> type;

    @JsonCreator
    public ReportTypeTagRequest(
        @JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_TYPE) Optional<ReportTypeTagType> type) {
        this.name = name;
        this.type = type;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_TYPE)
    public Optional<ReportTypeTagType> getType() {
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
        ReportTypeTagRequest that = (ReportTypeTagRequest) object;
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
        private Optional<ReportTypeTagType> type = Optional.empty();

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(ReportTypeTagType type) {
            this.type = Optional.ofNullable(type);
            return this;
        }

        public ReportTypeTagRequest build() {
            return new ReportTypeTagRequest(name, type);
        }
    }
}
