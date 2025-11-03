package com.extole.client.rest.client.domain.pattern;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.domain.pattern.ClientDomainPattern;
import com.extole.api.client.domain.pattern.ClientDomainPatternBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementResponse;
import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.program.ProgramResponse;
import com.extole.common.lang.ToString;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class ClientDomainPatternResponse extends ComponentElementResponse {

    private static final String ID = "id";
    private static final String PATTERN = "pattern";
    private static final String TYPE = "type";
    private static final String CLIENT_DOMAIN_ID = "client_domain_id";
    private static final String TEST = "test";

    private final Id<ClientDomainPattern> id;
    private final BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String> pattern;
    private final ClientDomainPatternType type;
    private final Optional<Id<ProgramResponse>> clientDomainId;
    private final BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean> test;

    public ClientDomainPatternResponse(@JsonProperty(ID) Id<ClientDomainPattern> id,
        @JsonProperty(PATTERN) BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String> pattern,
        @JsonProperty(TYPE) ClientDomainPatternType type,
        @JsonProperty(CLIENT_DOMAIN_ID) Optional<Id<ProgramResponse>> clientDomainId,
        @JsonProperty(TEST) BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean> test,
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
    public BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String> getPattern() {
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
    public BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean> getTest() {
        return test;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
