package com.extole.client.rest.prehandler.condition.response;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.client.rest.prehandler.condition.PrehandlerConditionType;

@Schema(description = "Condition that checks the data name exists.")
public class DataExistsPrehandlerConditionResponse extends PrehandlerConditionResponse {
    static final String TYPE = "DATA_EXISTS";

    private static final String JSON_DATA_KEYS = "data_keys";

    private final Set<String> dataKeys;

    @JsonCreator
    public DataExistsPrehandlerConditionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_DATA_KEYS) Set<String> dataKeys) {
        super(id, PrehandlerConditionType.DATA_EXISTS);
        this.dataKeys = dataKeys;
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerConditionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_DATA_KEYS)
    @Schema(nullable = false)
    public Set<String> getDataKeys() {
        return this.dataKeys;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Set<String> dataKeys;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withDataKeys(Set<String> dataKeys) {
            this.dataKeys = dataKeys;
            return this;
        }

        public DataExistsPrehandlerConditionResponse build() {
            return new DataExistsPrehandlerConditionResponse(id, dataKeys);
        }
    }
}
