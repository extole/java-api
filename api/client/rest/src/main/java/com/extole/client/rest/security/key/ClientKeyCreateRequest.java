package com.extole.client.rest.security.key;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class ClientKeyCreateRequest extends ComponentElementRequest {

    protected static final String TYPE = "type";
    protected static final String NAME = "name";
    protected static final String ALGORITHM = "algorithm";
    protected static final String DESCRIPTION = "description";
    protected static final String PARTNER_KEY_ID = "partner_key_id";
    protected static final String TAGS = "tags";

    private final ClientKeyType type;
    private final ClientKeyAlgorithm algorithm;
    private final BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name;
    private final BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description;
    private final Optional<String> partnerKeyId;
    private final Omissible<Set<String>> tags;

    public ClientKeyCreateRequest(ClientKeyType type,
        ClientKeyAlgorithm algorithm,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name,
        BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description,
        Optional<String> partnerKeyId,
        Omissible<Set<String>> tags,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.type = type;
        this.algorithm = algorithm;
        this.name = name;
        this.description = description;
        this.partnerKeyId = partnerKeyId;
        this.tags = tags;
    }

    @JsonProperty(TYPE)
    public ClientKeyType getType() {
        return type;
    }

    @JsonProperty(NAME)
    public BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> getName() {
        return name;
    }

    @JsonProperty(ALGORITHM)
    public ClientKeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    @JsonProperty(PARTNER_KEY_ID)
    public Optional<String> getPartnerKeyId() {
        return partnerKeyId;
    }

    @JsonProperty(DESCRIPTION)
    public BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> getDescription() {
        return description;
    }

    @JsonProperty(TAGS)
    public Omissible<Set<String>> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public abstract static class Builder<REQUEST extends ClientKeyCreateRequest, BUILDER extends Builder<REQUEST,
        BUILDER>>
        extends ComponentElementRequest.Builder<BUILDER> {

        protected ClientKeyType type;
        protected BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name;
        protected BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description =
            Provided.optionalEmpty();
        protected Optional<String> partnerKeyId = Optional.empty();
        protected Omissible<Set<String>> tags = Omissible.omitted();

        protected Builder() {
        }

        public BUILDER withType(ClientKeyType type) {
            this.type = type;
            return (BUILDER) this;
        }

        public BUILDER withName(BuildtimeEvaluatable<ClientKeyBuildtimeContext, String> name) {
            this.name = name;
            return (BUILDER) this;
        }

        public BUILDER withDescription(BuildtimeEvaluatable<ClientKeyBuildtimeContext, Optional<String>> description) {
            this.description = description;
            return (BUILDER) this;
        }

        public BUILDER withPartnerKeyId(Optional<String> partnerKeyId) {
            this.partnerKeyId = partnerKeyId;
            return (BUILDER) this;
        }

        public BUILDER withTags(Omissible<Set<String>> tags) {
            this.tags = tags;
            return (BUILDER) this;
        }

        public abstract REQUEST build();

    }
}
