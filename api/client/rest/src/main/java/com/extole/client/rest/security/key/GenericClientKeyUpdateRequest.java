package com.extole.client.rest.security.key;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class GenericClientKeyUpdateRequest extends ClientKeyUpdateRequest {

    protected static final String KEY = "key";
    private final Omissible<String> key;

    @JsonCreator
    public GenericClientKeyUpdateRequest(
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(KEY) Omissible<String> key,
        @JsonProperty(NAME) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> name,
        @JsonProperty(DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(PARTNER_KEY_ID) Omissible<String> partnerKeyId) {
        super(algorithm, name, description, componentIds, componentReferences, partnerKeyId);
        this.key = key;
    }

    @JsonProperty(KEY)
    public Omissible<String> getKey() {
        return key;
    }

    public static final Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ClientKeyUpdateRequest.Builder<GenericClientKeyUpdateRequest, Builder> {

        private ClientKeyAlgorithm algorithm;
        private Omissible<String> key = Omissible.omitted();

        private Builder() {
        }

        public Builder withAlgorithm(ClientKeyAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder withKey(String key) {
            this.key = Omissible.of(key);
            return this;
        }

        @Override
        public GenericClientKeyUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new GenericClientKeyUpdateRequest(algorithm, key, name, description, componentIds,
                componentReferences, partnerKeyId);
        }

    }

}
