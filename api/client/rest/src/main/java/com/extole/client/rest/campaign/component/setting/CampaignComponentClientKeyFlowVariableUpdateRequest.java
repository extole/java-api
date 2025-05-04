package com.extole.client.rest.campaign.component.setting;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.VariableBuildtimeContext;
import com.extole.api.campaign.VariableDescriptionBuildtimeContext;
import com.extole.api.client.security.key.ClientKey;
import com.extole.common.rest.omissible.Omissible;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public final class CampaignComponentClientKeyFlowVariableUpdateRequest
    extends CampaignComponentVariableUpdateRequest {
    public static final String SETTING_TYPE = "CLIENT_KEY_FLOW";

    private static final String JSON_REDIRECT_URI = "redirect_uri";
    private static final String JSON_CLIENT_KEY_URL = "client_key_url";
    private static final String JSON_CLIENT_KEY_OAUTH_FLOW = "client_key_oauth_flow";

    private final Omissible<String> redirectUri;
    private final Omissible<String> clientKeyUrl;
    private final Omissible<Id<ClientKey>> clientKeyOauthFlow;

    @JsonCreator
    private CampaignComponentClientKeyFlowVariableUpdateRequest(
        @JsonProperty(JSON_COMPONENT_SETTING_NAME) Omissible<String> name,
        @JsonProperty(JSON_COMPONENT_SETTING_DISPLAY_NAME) Omissible<Optional<String>> displayName,
        @JsonProperty(JSON_COMPONENT_VARIABLE_VALUES) Omissible<Map<String,
            BuildtimeEvaluatable<VariableBuildtimeContext,
                RuntimeEvaluatable<Object, Optional<Object>>>>> values,
        @JsonProperty(JSON_COMPONENT_VARIABLE_SOURCE) Omissible<VariableSource> source,
        @JsonProperty(JSON_COMPONENT_VARIABLE_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<VariableDescriptionBuildtimeContext, Optional<String>>> description,
        @JsonProperty(JSON_COMPONENT_SETTING_TAGS) Omissible<Set<String>> tags,
        @JsonProperty(JSON_COMPONENT_SETTING_PRIORITY) Omissible<DeweyDecimal> priority,
        @JsonProperty(JSON_REDIRECT_URI) Omissible<String> redirectUri,
        @JsonProperty(JSON_CLIENT_KEY_URL) Omissible<String> clientKeyUrl,
        @JsonProperty(JSON_CLIENT_KEY_OAUTH_FLOW) Omissible<Id<ClientKey>> clientKeyOauthFlow) {
        super(name, displayName, SettingType.CLIENT_KEY_FLOW, values, source, description, tags, priority);
        this.redirectUri = redirectUri;
        this.clientKeyUrl = clientKeyUrl;
        this.clientKeyOauthFlow = clientKeyOauthFlow;
    }

    @JsonProperty(JSON_REDIRECT_URI)
    public Omissible<String> getRedirectUri() {
        return redirectUri;
    }

    @JsonProperty(JSON_CLIENT_KEY_URL)
    public Omissible<String> getClientKeyUrl() {
        return clientKeyUrl;
    }

    @JsonProperty(JSON_CLIENT_KEY_OAUTH_FLOW)
    public Omissible<Id<ClientKey>> getClientKeyOauthFlow() {
        return clientKeyOauthFlow;
    }

    public static Builder<?, ?> builder() {
        return new Builder<>();
    }

    public static <CALLER> Builder<CALLER, ?> builder(CALLER caller) {
        return new Builder<>(caller);
    }

    public static final class Builder<CALLER, BUILDER_TYPE extends Builder<CALLER, BUILDER_TYPE>>
        extends CampaignComponentVariableUpdateRequest.Builder<CALLER,
            CampaignComponentClientKeyFlowVariableUpdateRequest, Builder<CALLER, BUILDER_TYPE>> {

        private Omissible<String> redirectUri = Omissible.omitted();
        private Omissible<String> clientKeyUrl = Omissible.omitted();
        private Omissible<Id<ClientKey>> clientKeyOauthFlow = Omissible.omitted();

        private Builder() {
            super();
        }

        private Builder(CALLER caller) {
            super(caller);
        }

        public BUILDER_TYPE withRedirectUri(String redirectUri) {
            this.redirectUri = Omissible.of(redirectUri);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withClientKeyUrl(String clientKeyUrl) {
            this.clientKeyUrl = Omissible.of(clientKeyUrl);
            return (BUILDER_TYPE) this;
        }

        public BUILDER_TYPE withClientKeyOauthFlow(Id<ClientKey> clientKeyOauthFlow) {
            this.clientKeyOauthFlow = Omissible.of(clientKeyOauthFlow);
            return (BUILDER_TYPE) this;
        }

        public CampaignComponentClientKeyFlowVariableUpdateRequest build() {
            return new CampaignComponentClientKeyFlowVariableUpdateRequest(
                name,
                displayName,
                values,
                source,
                description,
                tags,
                priority,
                redirectUri,
                clientKeyUrl,
                clientKeyOauthFlow);
        }
    }

}
