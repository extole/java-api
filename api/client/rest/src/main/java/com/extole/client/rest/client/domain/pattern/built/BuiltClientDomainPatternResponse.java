package com.extole.client.rest.client.domain.pattern.built;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.domain.pattern.ClientDomainPattern;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.client.domain.pattern.ClientDomainPatternType;
import com.extole.client.rest.program.ProgramResponse;
import com.extole.common.lang.ToString;
import com.extole.id.Id;

public class BuiltClientDomainPatternResponse extends ComponentElementResponse {

    private static final String ID = "id";
    private static final String PATTERN = "pattern";
    private static final String TYPE = "type";
    private static final String CLIENT_DOMAIN_ID = "client_domain_id";
    private static final String TEST = "test";

    private final Id<ClientDomainPattern> id;
    private final String pattern;
    private final ClientDomainPatternType type;
    private final Optional<Id<ProgramResponse>> clientDomainId;
    private final boolean test;

    public BuiltClientDomainPatternResponse(@JsonProperty(ID) Id<ClientDomainPattern> id,
        @JsonProperty(PATTERN) String pattern,
        @JsonProperty(TYPE) ClientDomainPatternType type,
        @JsonProperty(CLIENT_DOMAIN_ID) Optional<Id<ProgramResponse>> clientDomainId,
        @JsonProperty(TEST) boolean test,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(componentReferences, componentIds);
        this.id = id;
        this.pattern = pattern;
        this.type = type;
        this.clientDomainId = clientDomainId;
        this.test = test;
    }

    @JsonProperty(ID)
    public Id<ClientDomainPattern> getId() {
        return id;
    }

    @JsonProperty(PATTERN)
    public String getPattern() {
        return pattern;
    }

    @JsonProperty(TYPE)
    public ClientDomainPatternType getType() {
        return type;
    }

    @JsonProperty(CLIENT_DOMAIN_ID)
    public Optional<Id<ProgramResponse>> getClientDomainId() {
        return clientDomainId;
    }

    @JsonProperty(TEST)
    public boolean isTest() {
        return test;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
