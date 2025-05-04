package com.extole.client.rest.dimension;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DimensionMappingRequest {
    private static final String PROGRAM_LABEL = "program_label";
    private static final String DIMENSION = "dimension";
    private static final String MAPPING_VALUE_FROM = "value_from";
    private static final String MAPPING_VALUE_TO = "value_to";

    private final Optional<String> programLabel;
    private final String dimension;
    private final String valueFrom;
    private final String valueTo;

    public DimensionMappingRequest(@Nullable @JsonProperty(PROGRAM_LABEL) String programLabel,
        @JsonProperty(DIMENSION) String dimension,
        @JsonProperty(MAPPING_VALUE_FROM) String valueFrom,
        @JsonProperty(MAPPING_VALUE_TO) String valueTo) {
        this.programLabel = Optional.ofNullable(programLabel);
        this.dimension = dimension;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
    }

    @JsonProperty(PROGRAM_LABEL)
    public Optional<String> getProgramLabel() {
        return programLabel;
    }

    @JsonProperty(DIMENSION)
    public String getDimension() {
        return dimension;
    }

    @JsonProperty(MAPPING_VALUE_FROM)
    public String getValueFrom() {
        return valueFrom;
    }

    @JsonProperty(MAPPING_VALUE_TO)
    public String getValueTo() {
        return valueTo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Optional<String> programLabel = Optional.empty();
        private String dimension;
        private String valueFrom;
        private String valueTo;

        private Builder() {
        }

        public Builder withProgramLabel(String programLabel) {
            this.programLabel = Optional.ofNullable(programLabel);
            return this;
        }

        public Builder withDimension(String dimension) {
            this.dimension = dimension;
            return this;
        }

        public Builder withValueFrom(String valueFrom) {
            this.valueFrom = valueFrom;
            return this;
        }

        public Builder withValueTo(String valueTo) {
            this.valueTo = valueTo;
            return this;
        }

        public DimensionMappingRequest build() {
            return new DimensionMappingRequest(programLabel.orElse(null), dimension, valueFrom, valueTo);
        }
    }
}
