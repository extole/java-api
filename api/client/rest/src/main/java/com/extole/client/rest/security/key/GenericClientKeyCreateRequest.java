package com.extole.client.rest.security.key;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class GenericClientKeyCreateRequest extends StringBasedClientKeyCreateRequest {

    @JsonCreator
    public GenericClientKeyCreateRequest(
        @JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) String key,
        @JsonProperty(NAME) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        @JsonProperty(PARTNER_KEY_ID) Optional<String> partnerKeyId,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(type, algorithm, key, name, description, partnerKeyId, tags, componentIds, componentReferences);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends
        StringBasedClientKeyCreateRequest.Builder<GenericClientKeyCreateRequest, Builder> {

        private ClientKeyAlgorithm algorithm;

        private Builder() {
        }

        public Builder withAlgorithm(ClientKeyAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        @Override
        public GenericClientKeyCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new GenericClientKeyCreateRequest(type, algorithm, key, name, description, partnerKeyId, tags,
                componentIds, componentReferences);
        }

    }

}
