package com.extole.client.rest.prehandler.action.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerContext;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.evaluateable.Evaluatable;

@Schema(description = "Action that modifies the event data.")
public class SetDataPrehandlerActionRequest extends PrehandlerActionRequest {
    static final String TYPE = "SET_DATA";

    private static final String JSON_DATA = "data";
    private static final String JSON_DEFAULT_DATA = "default_data";
    private static final String JSON_DELETE_DATA = "delete_data";

    private final Map<String, Evaluatable<PrehandlerContext, String>> data;
    private final Map<String, Evaluatable<PrehandlerContext, String>> defaultData;
    private final Set<String> deleteData;

    @JsonCreator
    public SetDataPrehandlerActionRequest(
        @Nullable @JsonProperty(JSON_DATA) Map<String, Evaluatable<PrehandlerContext, String>> data,
        @Nullable @JsonProperty(JSON_DEFAULT_DATA) Map<String, Evaluatable<PrehandlerContext, String>> defaultData,
        @Nullable @JsonProperty(JSON_DELETE_DATA) Set<String> deleteData) {
        super(PrehandlerActionType.SET_DATA);
        this.data = data != null ? Collections.unmodifiableMap(new HashMap<>(data)) : Collections.emptyMap();
        this.defaultData =
            defaultData != null ? Collections.unmodifiableMap(new HashMap<>(defaultData)) : Collections.emptyMap();
        this.deleteData =
            deleteData != null ? Collections.unmodifiableSet(new HashSet<>(deleteData)) : Collections.emptySet();
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, required = true, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_DATA)
    @Schema(nullable = true, description = "Data to be set to the event. Data names (keys) are not case sensitive.")
    public Map<String, Evaluatable<PrehandlerContext, String>> getData() {
        return data;
    }

    @JsonProperty(JSON_DEFAULT_DATA)
    @Schema(nullable = true, description = "Default data to be set to the event. Only data (keys) that are not already"
        + " present in the event will be set. Data names (keys) are not case sensitive.")
    public Map<String, Evaluatable<PrehandlerContext, String>> getDefaultData() {
        return defaultData;
    }

    @JsonProperty(JSON_DELETE_DATA)
    @Schema(nullable = true, description = "Names (keys) of the data that should be removed from the event."
        + " Data names (keys) are not case sensitive.")
    public Set<String> getDeleteData() {
        return this.deleteData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, Evaluatable<PrehandlerContext, String>> data;
        private Map<String, Evaluatable<PrehandlerContext, String>> defaultData;
        private Set<String> deleteData;

        public Builder withData(Map<String, Evaluatable<PrehandlerContext, String>> data) {
            this.data = data;
            return this;
        }

        public Builder withDefaultData(Map<String, Evaluatable<PrehandlerContext, String>> defaultData) {
            this.defaultData = defaultData;
            return this;
        }

        public Builder withDeleteData(Set<String> deleteData) {
            this.deleteData = deleteData;
            return this;
        }

        public SetDataPrehandlerActionRequest build() {
            return new SetDataPrehandlerActionRequest(data, defaultData, deleteData);
        }
    }
}
