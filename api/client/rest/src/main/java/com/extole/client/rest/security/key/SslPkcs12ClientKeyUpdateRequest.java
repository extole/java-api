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

public class SslPkcs12ClientKeyUpdateRequest extends ClientKeyUpdateRequest {

    public static final String ALGORITHM_NAME_SSL_PKCS_12 = "SSL_PKCS_12";

    @JsonCreator
    public SslPkcs12ClientKeyUpdateRequest(
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(NAME) Omissible<BuildtimeEvaluatable<ClientKeyBuildtimeContext, String>> name,
        @JsonProperty(DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(PARTNER_KEY_ID) Omissible<String> partnerKeyId) {
        super(algorithm, name, description, componentIds, componentReferences, partnerKeyId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ClientKeyUpdateRequest.Builder<SslPkcs12ClientKeyUpdateRequest, Builder> {

        private Builder() {
        }

        @Override
        public SslPkcs12ClientKeyUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new SslPkcs12ClientKeyUpdateRequest(ClientKeyAlgorithm.SSL_PKCS_12, name, description, componentIds,
                componentReferences, partnerKeyId);
        }

    }

}
