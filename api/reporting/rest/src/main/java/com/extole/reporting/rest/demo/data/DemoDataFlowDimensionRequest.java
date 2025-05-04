package com.extole.reporting.rest.demo.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;

public class DemoDataFlowDimensionRequest {

    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String PROBABILITY = "probability";

    private final String name;
    private final String value;
    private final Double probability;

    public DemoDataFlowDimensionRequest(@JsonProperty(NAME) String name,
        @JsonProperty(VALUE) String value,
        @JsonProperty(PROBABILITY) Double probability) {
        this.name = name;
        this.value = value;
        this.probability = probability;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(PROBABILITY)
    public Double getProbability() {
        return probability;
    }

    @JsonProperty(VALUE)
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String value;
        private Double probability;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withValue(String value) {
            this.value = value;
            return this;
        }

        public Builder withProbability(Double probability) {
            this.probability = probability;
            return this;
        }

        public DemoDataFlowDimensionRequest build() {
            return new DemoDataFlowDimensionRequest(name, value, probability);
        }
    }
}
