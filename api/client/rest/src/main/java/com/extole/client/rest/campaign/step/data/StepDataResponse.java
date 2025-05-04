package com.extole.client.rest.campaign.step.data;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.step.data.StepDataContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class StepDataResponse extends ComponentElementResponse {
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_DIMENSION = "dimension";
    private static final String JSON_PERSIST_TYPES = "persist_types";
    private static final String JSON_DEFAULT_VALUE = "default_value";
    private static final String JSON_KEY_TYPE = "key_type";
    private static final String JSON_ENABLED = "enabled";

    private final String id;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext,
        RuntimeEvaluatable<StepDataContext, Optional<Object>>> value;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope> scope;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> dimension;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, List<StepDataPersistType>> persistTypes;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext,
        RuntimeEvaluatable<StepDataContext, Optional<Object>>> defaultValue;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType> keyType;
    private final BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled;

    public StepDataResponse(
        @JsonProperty(JSON_ID) String id,
        @JsonProperty(JSON_NAME) BuildtimeEvaluatable<CampaignBuildtimeContext, String> name,
        @JsonProperty(JSON_VALUE) BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>> value,
        @JsonProperty(JSON_SCOPE) BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope> scope,
        @JsonProperty(JSON_DIMENSION) BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> dimension,
        @JsonProperty(JSON_PERSIST_TYPES) BuildtimeEvaluatable<CampaignBuildtimeContext,
            List<StepDataPersistType>> persistTypes,
        @JsonProperty(JSON_DEFAULT_VALUE) BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>> defaultValue,
        @JsonProperty(JSON_KEY_TYPE) BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType> keyType,
        @JsonProperty(JSON_ENABLED) BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.name = name;
        this.value = value;
        this.scope = scope;
        this.dimension = dimension;
        this.persistTypes = persistTypes;
        this.defaultValue = defaultValue;
        this.keyType = keyType;
        this.enabled = enabled;
    }

    @JsonProperty(JSON_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_NAME)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Optional<Object>>>
        getValue() {
        return value;
    }

    @JsonProperty(JSON_SCOPE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope> getScope() {
        return scope;
    }

    @JsonProperty(JSON_DIMENSION)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> isDimension() {
        return dimension;
    }

    @JsonProperty(JSON_PERSIST_TYPES)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, List<StepDataPersistType>> getPersistTypes() {
        return persistTypes;
    }

    @JsonProperty(JSON_DEFAULT_VALUE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Optional<Object>>>
        getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty(JSON_KEY_TYPE)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType> getKeyType() {
        return keyType;
    }

    @JsonProperty(JSON_ENABLED)
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
