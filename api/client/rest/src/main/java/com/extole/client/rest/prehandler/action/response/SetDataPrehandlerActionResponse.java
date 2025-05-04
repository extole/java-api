package com.extole.client.rest.prehandler.action.response;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.prehandler.PrehandlerContext;
import com.extole.client.rest.prehandler.action.PrehandlerActionType;
import com.extole.evaluateable.Evaluatable;

@Schema(description = "Action that modifies the event data.")
public class SetDataPrehandlerActionResponse extends PrehandlerActionResponse {
    static final String TYPE = "SET_DATA";

    private static final String JSON_DATA = "data";
    private static final String JSON_DEFAULT_DATA = "default_data";
    private static final String JSON_DELETE_DATA = "delete_data";

    private final Map<String, Object> data;
    private final Map<String, Object> defaultData;
    private final Set<String> deleteData;

    @JsonCreator
    public SetDataPrehandlerActionResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_DATA) Map<String, Evaluatable<PrehandlerContext, String>> data,
        @JsonProperty(JSON_DEFAULT_DATA) Map<String, Evaluatable<PrehandlerContext, String>> defaultData,
        @JsonProperty(JSON_DELETE_DATA) Set<String> deleteData) {
        super(id, PrehandlerActionType.SET_DATA);
        this.data = data != null ? Collections.unmodifiableMap(new HashMap<>(data)) : Collections.emptyMap();
        this.defaultData =
            defaultData != null ? Collections.unmodifiableMap(new HashMap<>(defaultData)) : Collections.emptyMap();
        this.deleteData =
            deleteData != null ? Collections.unmodifiableSet(new HashSet<>(deleteData)) : Collections.emptySet();
    }

    @Override
    @JsonProperty(JSON_TYPE)
    @Schema(defaultValue = TYPE, nullable = false)
    public PrehandlerActionType getType() {
        return super.getType();
    }

    @JsonProperty(JSON_DATA)
    @Schema(nullable = true)
    public Map<String, Object> getData() {
        return data;
    }

    @JsonProperty(JSON_DEFAULT_DATA)
    @Schema(nullable = true)
    public Map<String, Object> getDefaultData() {
        return defaultData;
    }

    @JsonProperty(JSON_DELETE_DATA)
    @Schema(nullable = true)
    public Set<String> getDeleteData() {
        return this.deleteData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Map<String, Evaluatable<PrehandlerContext, String>> data;
        private Map<String, Evaluatable<PrehandlerContext, String>> defaultData;
        private Set<String> deleteData;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

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

        public SetDataPrehandlerActionResponse build() {
            return new SetDataPrehandlerActionResponse(id, data, defaultData, deleteData);
        }
    }
}
