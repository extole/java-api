package com.extole.client.rest.campaign.controller.action.creative;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerActionCreativeUpdateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_CLASSIFICATION = "classification";
    private static final String JSON_API_VERSION = "api_version";
    private static final String JSON_THEME_VERSION = "theme_version";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<Classification> classification;
    private final Omissible<Integer> creativeApiVersion;
    private final Omissible<String> themeVersion;

    @JsonCreator
    private CampaignControllerActionCreativeUpdateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_CLASSIFICATION) Omissible<Classification> classification,
        @JsonProperty(JSON_API_VERSION) Omissible<Integer> creativeApiVersion,
        @JsonProperty(JSON_THEME_VERSION) Omissible<String> themeVersion) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.classification = classification;
        this.creativeApiVersion = creativeApiVersion;
        this.themeVersion = themeVersion;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_CLASSIFICATION)
    public Omissible<Classification> getClassification() {
        return classification;
    }

    @JsonProperty(JSON_API_VERSION)
    public Omissible<Integer> getCreativeApiVersion() {
        return creativeApiVersion;
    }

    @JsonProperty(JSON_THEME_VERSION)
    public Omissible<String> getThemeVersion() {
        return themeVersion;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<Classification> classification = Omissible.omitted();
        private Omissible<Integer> creativeApiVersion = Omissible.omitted();
        private Omissible<String> themeVersion = Omissible.omitted();

        private Builder() {
        }

        public Builder withQuality(CampaignControllerActionQuality quality) {
            this.quality = Omissible.of(quality);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withClassification(Classification classification) {
            this.classification = Omissible.of(classification);
            return this;
        }

        public Builder withCreativeApiVersion(Integer creativeApiVersion) {
            this.creativeApiVersion = Omissible.of(creativeApiVersion);
            return this;
        }

        public Builder withThemeVersion(String themeVersion) {
            this.themeVersion = Omissible.of(themeVersion);
            return this;
        }

        public CampaignControllerActionCreativeUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionCreativeUpdateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                classification,
                creativeApiVersion,
                themeVersion);
        }

    }

}
