package com.extole.reporting.rest.demo.data;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.common.lang.ToString;

public class DemoDataFlowRequest {

    public enum JourneyType {
        ADVOCATE, FRIEND
    }

    private static final String EVENT_NAME = "event_name";
    private static final String PROBABILITY = "probability";
    private static final String PERSON_SIDE = "person_side";
    private static final String DIMENSIONS = "dimensions";

    private final String eventName;
    private final Double probability;
    private final JourneyType journeyType;
    private final List<DemoDataFlowDimensionRequest> dimensions;

    public DemoDataFlowRequest(
        @JsonProperty(EVENT_NAME) String eventName,
        @JsonProperty(PROBABILITY) Double probability,
        @Nullable @JsonProperty(PERSON_SIDE) JourneyType journeyType,
        @JsonProperty(DIMENSIONS) List<DemoDataFlowDimensionRequest> dimensions) {
        this.eventName = eventName;
        this.probability = probability;
        this.journeyType = journeyType;
        this.dimensions = dimensions != null ? ImmutableList.copyOf(dimensions) : ImmutableList.of();
    }

    @JsonProperty(EVENT_NAME)
    public String getEventName() {
        return eventName;
    }

    @JsonProperty(PROBABILITY)
    public Double getProbability() {
        return probability;
    }

    @JsonProperty(PERSON_SIDE)
    public Optional<JourneyType> getJourneyType() {
        return Optional.ofNullable(journeyType);
    }

    @JsonProperty(DIMENSIONS)
    public List<DemoDataFlowDimensionRequest> getDimensions() {
        return dimensions;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String eventName;
        private Double probability;
        private JourneyType journeyType;
        private List<DemoDataFlowDimensionRequest> dimensions;

        private Builder() {
        }

        public Builder withEventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder withProbability(Double probability) {
            this.probability = probability;
            return this;
        }

        public Builder withPersonSide(JourneyType journeyType) {
            this.journeyType = journeyType;
            return this;
        }

        public Builder withDimensions(List<DemoDataFlowDimensionRequest> dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public DemoDataFlowRequest build() {
            return new DemoDataFlowRequest(eventName, probability, journeyType, dimensions);
        }
    }
}
