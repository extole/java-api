package com.extole.client.rest.campaign.component.setting;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public final class BatchComponentVariableValues {

    private static final String FIELD_ABSOLUTE_PATH = "absolute_name";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DISPLAY_NAME = "display_name";
    private static final String FIELD_TYPE = "type";

    private final String componentAbsolutePath;
    private final String name;
    private final Optional<String> displayName;
    private final SettingType settingType;
    @JsonIgnore
    private final Map<String,
        BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values =
            Maps.newHashMap();

    @JsonCreator
    public BatchComponentVariableValues(
        @JsonProperty(FIELD_ABSOLUTE_PATH) String componentAbsolutePath,
        @JsonProperty(FIELD_NAME) String name,
        @JsonProperty(FIELD_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(FIELD_TYPE) SettingType settingType) {
        this(componentAbsolutePath, name, displayName, settingType, Collections.emptyMap());
    }

    public BatchComponentVariableValues(
        String componentAbsolutePath,
        String name,
        Optional<String> displayName,
        SettingType settingType,
        Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values) {
        this.componentAbsolutePath = componentAbsolutePath;
        this.name = name;
        this.displayName = displayName;
        this.settingType = settingType;
        this.values.putAll(values);
    }

    @JsonProperty(FIELD_ABSOLUTE_PATH)
    public String getComponentAbsolutePath() {
        return componentAbsolutePath;
    }

    @JsonProperty(FIELD_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(FIELD_DISPLAY_NAME)
    public Optional<String> getDisplayName() {
        return displayName;
    }

    @JsonProperty(FIELD_TYPE)
    public SettingType getSettingType() {
        return settingType;
    }

    @JsonAnySetter
    private void setValuesProperty(String key,
        BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>> value) {
        values.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>>
        getValues() {
        return ImmutableMap.copyOf(values);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static BatchVariableValuesResponseBuilder builder(BatchComponentVariableValues variable) {
        return new BatchVariableValuesResponseBuilder(variable);
    }

    public static BatchVariableValuesResponseBuilder builder() {
        return new BatchVariableValuesResponseBuilder();
    }

    public static final class BatchVariableValuesResponseBuilder {

        private String componentAbsoluteName;
        private String name;
        private Optional<String> displayName = Optional.empty();
        private SettingType settingType;
        private Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values =
                Collections.emptyMap();

        private BatchVariableValuesResponseBuilder(BatchComponentVariableValues variable) {
            this.componentAbsoluteName = variable.getComponentAbsolutePath();
            this.name = variable.getName();
            this.displayName = variable.getDisplayName();
            this.settingType = variable.getSettingType();
            this.values = ImmutableMap.copyOf(variable.getValues());
        }

        private BatchVariableValuesResponseBuilder() {
        }

        public BatchVariableValuesResponseBuilder withComponentAbsoluteName(String componentAbsoluteName) {
            this.componentAbsoluteName = componentAbsoluteName;
            return this;
        }

        public BatchVariableValuesResponseBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public BatchVariableValuesResponseBuilder withDisplayName(Optional<String> displayName) {
            this.displayName = displayName;
            return this;
        }

        public BatchVariableValuesResponseBuilder withVariableType(SettingType settingType) {
            this.settingType = settingType;
            return this;
        }

        public BatchVariableValuesResponseBuilder withValues(
            Map<String,
                BuildtimeEvaluatable<VariableBuildtimeContext, RuntimeEvaluatable<Object, Optional<Object>>>> values) {
            this.values = ImmutableMap.copyOf(values);
            return this;
        }

        public BatchComponentVariableValues build() {
            return new BatchComponentVariableValues(componentAbsoluteName, name, displayName, settingType, values);
        }

    }

}
