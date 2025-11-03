package com.extole.client.rest.client.domain.pattern;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.client.domain.pattern.ClientDomainPatternBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.program.ProgramResponse;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class ClientDomainPatternUpdateRequest extends ComponentElementRequest {

    private static final String PATTERN = "pattern";
    private static final String TYPE = "type";
    private static final String CLIENT_DOMAIN_ID = "client_domain_id";
    private static final String TEST = "test";

    private final Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String>> pattern;
    private final Omissible<ClientDomainPatternType> type;
    private final Omissible<Optional<Id<ProgramResponse>>> clientDomainId;
    private final Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean>> test;

    public ClientDomainPatternUpdateRequest(
        @JsonProperty(PATTERN) Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String>> pattern,
        @JsonProperty(TYPE) Omissible<ClientDomainPatternType> type,
        @JsonProperty(CLIENT_DOMAIN_ID) Omissible<Optional<Id<ProgramResponse>>> clientDomainId,
        @JsonProperty(TEST) Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean>> test,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.pattern = pattern;
        this.type = type;
        this.clientDomainId = clientDomainId;
        this.test = test;
    }

    @JsonProperty(PATTERN)
    public Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String>> getPattern() {
        return pattern;
    }

    @JsonProperty(TYPE)
    public Omissible<ClientDomainPatternType> getType() {
        return type;
    }

    @JsonProperty(CLIENT_DOMAIN_ID)
    public Omissible<Optional<Id<ProgramResponse>>> getClientDomainId() {
        return clientDomainId;
    }

    @JsonProperty(TEST)
    public Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean>> getTest() {
        return test;
    }

    public static final Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String>> pattern =
            Omissible.omitted();
        private Omissible<ClientDomainPatternType> type = Omissible.omitted();
        private Omissible<Optional<Id<ProgramResponse>>> clientDomainId = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean>> test =
            Omissible.omitted();

        private Builder() {
        }

        public Builder withPattern(BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, String> pattern) {
            this.pattern = Omissible.of(pattern);
            return this;
        }

        public Builder withType(ClientDomainPatternType type) {
            this.type = Omissible.of(type);
            return this;
        }

        public Builder withClientDomainId(Id<ProgramResponse> clientDomainId) {
            this.clientDomainId = Omissible.of(Optional.of(clientDomainId));
            return this;
        }

        public Builder clearClientDomainId() {
            this.clientDomainId = Omissible.of(Optional.empty());
            return this;
        }

        public Builder withTest(BuildtimeEvaluatable<ClientDomainPatternBuildtimeContext, Boolean> test) {
            this.test = Omissible.of(test);
            return this;
        }

        public ClientDomainPatternUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new ClientDomainPatternUpdateRequest(pattern, type, clientDomainId, test, componentIds,
                componentReferences);
        }
    }
}
