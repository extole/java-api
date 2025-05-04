package com.extole.client.rest.prehandler.condition.request;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;

@Schema(description = "Condition that checks the data name exists.")
public class DataExistsPrehandlerConditionRequest extends PrehandlerConditionRequest {
    static final String TYPE = "DATA_EXISTS";

    private static final String JSON_DATA_KEYS = "data_keys";

    private final Set<String> dataKeys;

    @JsonCreator
    public DataExistsPrehandlerConditionRequest(@JsonProperty(JSON_DATA_KEYS) Set<String> dataKeys) {
        super(PrehandlerConditionType.DATA_EXISTS);
        this.dataKeys = dataKeys;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_DATA_KEYS)
    @Schema(required = true, nullable = false,
        description = "Condition evaluates to true if the data name is present in the event."
            + " Data name is not case sensitive.")
    public Set<String> getDataKeys() {
        return this.dataKeys;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Set<String> dataKeys;

        public Builder withDataKeys(Set<String> dataKeys) {
            this.dataKeys = dataKeys;
            return this;
        }

        public DataExistsPrehandlerConditionRequest build() {
            return new DataExistsPrehandlerConditionRequest(dataKeys);
        }
    }
}
