package com.extole.client.rest.campaign.controller.action.data.intelligence;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.provided.Provided;
import com.extole.id.Id;

public class CampaignControllerActionDataIntelligenceRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_INTELLIGENCE_PROVIDER = "intelligence_provider";
    private static final String JSON_EVENT_NAME = "event_name";
    private static final String JSON_PROFILE_RISK_UPDATE_INTERVAL = "profile_risk_update_interval";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<
        ControllerBuildtimeContext, DataIntelligenceProviderType>> intelligenceProviderType;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Duration>> profileRiskUpdateInterval;

    public CampaignControllerActionDataIntelligenceRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_INTELLIGENCE_PROVIDER) Omissible<BuildtimeEvaluatable<
            ControllerBuildtimeContext, DataIntelligenceProviderType>> intelligenceProviderType,
        @JsonProperty(JSON_EVENT_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName,
        @JsonProperty(JSON_PROFILE_RISK_UPDATE_INTERVAL) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Duration>> profileRiskUpdateInterval) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.intelligenceProviderType = intelligenceProviderType;
        this.eventName = eventName;
        this.profileRiskUpdateInterval = profileRiskUpdateInterval;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_INTELLIGENCE_PROVIDER)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, DataIntelligenceProviderType>>
        getIntelligenceProviderType() {
        return intelligenceProviderType;
    }

    @JsonProperty(JSON_EVENT_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> getEventName() {
        return eventName;
    }

    @JsonProperty(JSON_PROFILE_RISK_UPDATE_INTERVAL)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Duration>> getProfileRiskUpdateInterval() {
        return profileRiskUpdateInterval;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, DataIntelligenceProviderType>> intelligenceProvider =
                Omissible.omitted();
        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> eventName = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Duration>> profileRiskUpdateInterval =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();

        private Builder() {
        }

        public Builder withIntelligenceProvider(
            BuildtimeEvaluatable<ControllerBuildtimeContext, DataIntelligenceProviderType> intelligenceProvider) {
            this.intelligenceProvider = Omissible.of(intelligenceProvider);
            return this;
        }

        public Builder withIntelligenceProvider(DataIntelligenceProviderType intelligenceProvider) {
            this.intelligenceProvider = Omissible.of(Provided.of(intelligenceProvider));
            return this;
        }

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withEventName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> eventName) {
            this.eventName = Omissible.of(eventName);
            return this;
        }

        public Builder withEventName(String eventName) {
            this.eventName = Omissible.of(Provided.of(eventName));
            return this;
        }

        public Builder withProfileRiskUpdateInterval(
            BuildtimeEvaluatable<ControllerBuildtimeContext, Duration> profileRiskUpdateInterval) {
            this.profileRiskUpdateInterval = Omissible.of(profileRiskUpdateInterval);
            return this;
        }

        public Builder withProfileRiskUpdateInterval(Duration profileRiskUpdateInterval) {
            this.profileRiskUpdateInterval = Omissible.of(Provided.of(profileRiskUpdateInterval));
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public CampaignControllerActionDataIntelligenceRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionDataIntelligenceRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                intelligenceProvider,
                eventName,
                profileRiskUpdateInterval);
        }

    }

}
