package com.extole.client.rest.campaign.step.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.step.data.StepDataContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class StepDataUpdateRequest extends ComponentElementRequest {

    private static final String JSON_NAME = "name";
    private static final String JSON_VALUE = "value";
    private static final String JSON_SCOPE = "scope";
    private static final String JSON_DIMENSION = "dimension";
    private static final String JSON_PERSIST_TYPES = "persist_types";
    private static final String JSON_DEFAULT_VALUE = "default_value";
    private static final String JSON_KEY_TYPE = "key_type";
    private static final String JSON_ENABLED = "enabled";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
        RuntimeEvaluatable<StepDataContext, Optional<Object>>>> value;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope>> scope;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> dimension;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<StepDataPersistType>>> persistTypes;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
        RuntimeEvaluatable<StepDataContext, Optional<Object>>>> defaultValue;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType>> keyType;
    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled;

    public StepDataUpdateRequest(
        @JsonProperty(JSON_NAME) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name,
        @JsonProperty(JSON_VALUE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>>> value,
        @JsonProperty(JSON_SCOPE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope>> scope,
        @JsonProperty(JSON_DIMENSION) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> dimension,
        @JsonProperty(JSON_PERSIST_TYPES) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            List<StepDataPersistType>>> persistTypes,
        @JsonProperty(JSON_DEFAULT_VALUE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>>> defaultValue,
        @JsonProperty(JSON_KEY_TYPE) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType>> keyType,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.name = name;
        this.value = value;
        this.scope = scope;
        this.dimension = dimension;
        this.persistTypes = persistTypes;
        this.defaultValue = defaultValue;
        this.keyType = keyType;
        this.enabled = enabled;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(JSON_VALUE)
    public
        Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Optional<Object>>>>
        getValue() {
        return value;
    }

    @JsonProperty(JSON_SCOPE)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope>> getScope() {
        return scope;
    }

    @JsonProperty(JSON_DIMENSION)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> isDimension() {
        return dimension;
    }

    @JsonProperty(JSON_PERSIST_TYPES)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<StepDataPersistType>>> getPersistTypes() {
        return persistTypes;
    }

    @JsonProperty(JSON_DEFAULT_VALUE)
    public
        Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Optional<Object>>>>
        getDefaultValue() {
        return defaultValue;
    }

    @JsonProperty(JSON_KEY_TYPE)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType>> getKeyType() {
        return keyType;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    public static <T> Builder<T> builder(T caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<T> extends ComponentElementRequest.Builder<Builder<T>> {
        private final T caller;

        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>>> value = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope>> scope = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> dimension = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, List<StepDataPersistType>>> persistTypes =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>>> defaultValue = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType>> keyType =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled =
            Omissible.omitted();

        private Builder(T caller) {
            this.caller = caller;
        }

        public Builder<T> withName(BuildtimeEvaluatable<CampaignBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder<T> withValue(BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>> value) {
            this.value = Omissible.of(value);
            return this;
        }

        public Builder<T> withScope(BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataScope> scope) {
            this.scope = Omissible.of(scope);
            return this;
        }

        public Builder<T> withDimension(BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> dimension) {
            this.dimension = Omissible.of(dimension);
            return this;
        }

        public Builder<T> withPersistTypes(
            BuildtimeEvaluatable<CampaignBuildtimeContext, List<StepDataPersistType>> persistTypes) {
            this.persistTypes = Omissible.of(persistTypes);
            return this;
        }

        public Builder<T> withDefaultValue(BuildtimeEvaluatable<CampaignBuildtimeContext,
            RuntimeEvaluatable<StepDataContext, Optional<Object>>> defaultValue) {
            this.defaultValue = Omissible.of(defaultValue);
            return this;
        }

        public Builder<T> withKeyType(BuildtimeEvaluatable<CampaignBuildtimeContext, StepDataKeyType> keyType) {
            this.keyType = Omissible.of(keyType);
            return this;
        }

        public Builder<T> withEnabled(BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public T done() {
            return caller;
        }

        @Override
        public StepDataUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new StepDataUpdateRequest(name,
                value,
                scope,
                dimension,
                persistTypes,
                defaultValue,
                keyType,
                enabled,
                componentIds,
                componentReferences);
        }

    }

}
