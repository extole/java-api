package com.extole.client.rest.campaign.built.step.data;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import com.extole.api.step.data.StepDataContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.step.data.StepDataKeyType;
import com.extole.client.rest.campaign.step.data.StepDataPersistType;
import com.extole.client.rest.campaign.step.data.StepDataScope;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class BuiltStepDataResponse extends ComponentElementResponse {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_DIMENSION = "dimension";
    private static final String JSON_PERSIST_TYPES = "persist_types";
    private static final String JSON_DEFAULT_VALUE = "default_value";
    private static final String JSON_KEY_TYPE = "key_type";
    private static final String JSON_ENABLED = "enabled";

    private final String name;
    private final RuntimeEvaluatable<StepDataContext, Optional<Object>> value;
    private final StepDataScope scope;
    private final Boolean dimension;
    private final List<StepDataPersistType> persistTypes;
    private final RuntimeEvaluatable<StepDataContext, Optional<Object>> defaultValue;
    private final StepDataKeyType keyType;
    private final Boolean enabled;

    public BuiltStepDataResponse(@JsonProperty(JSON_NAME) String name,
        @JsonProperty(JSON_VALUE) RuntimeEvaluatable<StepDataContext, Optional<Object>> value,
        @JsonProperty(JSON_SCOPE) StepDataScope scope,
        @JsonProperty(JSON_DIMENSION) Boolean dimension,
        @JsonProperty(JSON_PERSIST_TYPES) List<StepDataPersistType> persistTypes,
        @JsonProperty(JSON_DEFAULT_VALUE) RuntimeEvaluatable<StepDataContext, Optional<Object>> defaultValue,
        @JsonProperty(JSON_KEY_TYPE) StepDataKeyType keyType,
        @JsonProperty(JSON_ENABLED) Boolean enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.value = value;
        this.scope = scope;
        this.dimension = dimension;
        this.persistTypes = persistTypes != null ? ImmutableList.copyOf(persistTypes) : ImmutableList.of();
        this.defaultValue = defaultValue;
        this.keyType = keyType;
        this.enabled = enabled;
    }

    @JsonProperty(JSON_NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public RuntimeEvaluatable<StepDataContext, Optional<Object>> getValue() {
        return value;
    }

    @JsonProperty(JSON_SCOPE)
    public StepDataScope getScope() {
        return scope;
    }

    @JsonProperty(JSON_DIMENSION)
    public Boolean isDimension() {
        return dimension;
    }

    @JsonProperty(JSON_PERSIST_TYPES)
    public List<StepDataPersistType> getPersistTypes() {
        return persistTypes;
    }

    @JsonProperty(JSON_DEFAULT_VALUE)
    public RuntimeEvaluatable<StepDataContext, Optional<Object>> getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty(JSON_KEY_TYPE)
    public StepDataKeyType getKeyType() {
        return keyType;
    }

    @JsonProperty(JSON_ENABLED)
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
