package com.extole.client.rest.campaign.component.setting;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;

public final class BatchComponentVariableUpdateRequest {
    private static final String FIELD_ABSOLUTE_PATH = "absolute_name";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DISPLAY_NAME = "display_name";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_VALUES = "values";

    private final String componentAbsolutePath;
    private final String name;
    private final Optional<String> displayName;
    private final SettingType settingType;
    private final Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
        RuntimeEvaluatable<Object, Optional<Object>>>> values;

    @JsonCreator
    private BatchComponentVariableUpdateRequest(
        @JsonProperty(FIELD_ABSOLUTE_PATH) String componentAbsolutePath,
        @JsonProperty(FIELD_NAME) String name,
        @JsonProperty(FIELD_DISPLAY_NAME) Optional<String> displayName,
        @JsonProperty(FIELD_TYPE) SettingType settingType,
        @JsonProperty(FIELD_VALUES) Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> values) {
        this.componentAbsolutePath = componentAbsolutePath;
        this.name = name;
        this.displayName = displayName;
        this.settingType = settingType;
        this.values = values;
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

    @JsonProperty(FIELD_VALUES)
    public Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
        RuntimeEvaluatable<Object, Optional<Object>>>> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String componentAbsolutePath = StringUtils.EMPTY;
        private String name = StringUtils.EMPTY;
        private Optional<String> displayName = Optional.empty();
        private SettingType settingType;
        private Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> values = Collections.emptyMap();

        private Builder() {
        }

        public Builder withComponentAbsolutePath(String componentAbsolutePath) {
            this.componentAbsolutePath = componentAbsolutePath;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(Optional<String> displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withVariableType(SettingType settingType) {
            this.settingType = settingType;
            return this;
        }

        public Builder withValues(Map<String, BuildtimeEvaluatable<VariableBuildtimeContext,
            RuntimeEvaluatable<Object, Optional<Object>>>> values) {
            this.values = Objects.requireNonNullElse(values, this.values);
            return this;
        }

        public BatchComponentVariableUpdateRequest build() {
            return new BatchComponentVariableUpdateRequest(componentAbsolutePath,
                name, displayName, settingType, values);
        }
    }

}
