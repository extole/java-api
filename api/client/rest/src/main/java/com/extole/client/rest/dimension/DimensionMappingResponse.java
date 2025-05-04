package com.extole.client.rest.dimension;

import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DimensionMappingResponse {
    private static final String ID = "id";
    private static final String PROGRAM_LABEL = "program_label";
    private static final String DIMENSION = "dimension";
    private static final String VALUE_FROM = "value_from";
    private static final String VALUE_TO = "value_to";

    private final String id;
    private final Optional<String> programLabel;
    private final String dimension;
    private final String valueFrom;
    private final String valueTo;

    public DimensionMappingResponse(@JsonProperty(ID) String id,
        @JsonProperty(PROGRAM_LABEL) Optional<String> programLabel,
        @JsonProperty(DIMENSION) String dimension,
        @JsonProperty(VALUE_FROM) String valueFrom,
        @JsonProperty(VALUE_TO) String valueTo) {
        this.id = id;
        this.programLabel = programLabel;
        this.dimension = dimension;
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
    }

    @JsonProperty(ID)
    public String getId() {
        return id;
    }

    @Nullable
    @JsonProperty(PROGRAM_LABEL)
    public String getProgramLabel() {
        return programLabel.orElse(null);
    }

    @JsonProperty(DIMENSION)
    public String getDimension() {
        return dimension;
    }

    @JsonProperty(VALUE_FROM)
    public String getValueFrom() {
        return valueFrom;
    }

    @JsonProperty(VALUE_TO)
    public String getValueTo() {
        return valueTo;
    }
}
