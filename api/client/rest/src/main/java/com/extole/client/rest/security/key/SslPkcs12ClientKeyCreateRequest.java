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

public class SslPkcs12ClientKeyCreateRequest extends FileBasedClientKeyCreateRequest {

    public static final String ALGORITHM_NAME_SSL_PKCS_12 = "SSL_PKCS_12";

    private static final String PASSWORD = "password";

    private final Omissible<String> password;

    @JsonCreator
    public SslPkcs12ClientKeyCreateRequest(
        @JsonProperty(TYPE) ClientKeyType type,
        @JsonProperty(ALGORITHM) ClientKeyAlgorithm algorithm,
        @JsonProperty(NAME) BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        @JsonProperty(DESCRIPTION) BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        @JsonProperty(PARTNER_KEY_ID) Optional<String> partnerKeyId,
        @JsonProperty(PASSWORD) Omissible<String> password,
        @JsonProperty(TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(type, algorithm, name, description, partnerKeyId, tags, componentIds, componentReferences);
        this.password = password;
    }

    @JsonProperty(PASSWORD)
    public Omissible<String> getPassword() {
        return password;
    }

    public static final Builder builder() {
        return new Builder();
    }

    public static final class Builder
        extends FileBasedClientKeyCreateRequest.Builder<SslPkcs12ClientKeyCreateRequest, Builder> {

        private Omissible<String> password = Omissible.omitted();

        private Builder() {
        }

        public Builder withPassword(String password) {
            this.password = Omissible.of(password);
            return this;
        }

        @Override
        public SslPkcs12ClientKeyCreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new SslPkcs12ClientKeyCreateRequest(type, ClientKeyAlgorithm.SSL_PKCS_12, name, description,
                partnerKeyId, password, tags, componentIds,
                componentReferences);
        }

    }

}
