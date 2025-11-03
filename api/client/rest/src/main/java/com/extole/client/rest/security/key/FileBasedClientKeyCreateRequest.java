package com.extole.client.rest.security.key;

import static com.extole.client.rest.security.key.ClientKeyCreateRequest.ALGORITHM;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = ALGORITHM, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SslPkcs12ClientKeyCreateRequest.class,
        name = SslPkcs12ClientKeyCreateRequest.ALGORITHM_NAME_SSL_PKCS_12)
})
public abstract class FileBasedClientKeyCreateRequest extends ClientKeyCreateRequest {

    public FileBasedClientKeyCreateRequest(
        ClientKeyType type,
        ClientKeyAlgorithm algorithm,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        Optional<String> partnerKeyId,
        Omissible<Set<String>> tags,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(type, algorithm, name, description, partnerKeyId, tags, componentIds, componentReferences);
    }

    public abstract static class Builder<REQUEST extends FileBasedClientKeyCreateRequest, BUILDER extends FileBasedClientKeyCreateRequest.Builder<
        REQUEST, BUILDER>>
        extends ClientKeyCreateRequest.Builder<REQUEST, BUILDER> {

        protected Builder() {
        }

        @Override
        public abstract REQUEST build();

    }

}
