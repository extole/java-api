package com.extole.client.rest.campaign.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerShareConfiguration extends CampaignControllerTriggerConfiguration {

    @Deprecated // TODO remove share_types ENG-10463
    private static final String JSON_SHARE_TYPES = "share_types";
    private static final String JSON_CHANNELS = "channels";
    private static final String JSON_QUALITY = "quality";

    private final Set<CampaignControllerTriggerShareChannel> channels;
    private final ShareQuality quality;

    public CampaignControllerTriggerShareConfiguration(
        @JsonProperty(TRIGGER_ID) Omissible<Id<CampaignControllerTriggerConfiguration>> triggerId,
        @JsonProperty(TRIGGER_PHASE) BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase> triggerPhase,
        @JsonProperty(TRIGGER_NAME) BuildtimeEvaluatable<ControllerBuildtimeContext, String> name,
        @JsonProperty(TRIGGER_DESCRIPTION) BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>> description,
        @JsonProperty(ENABLED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled,
        @JsonProperty(NEGATED) BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated,
        @JsonProperty(JSON_SHARE_TYPES) Set<CampaignControllerTriggerShareChannel> shareTypes,
        @JsonProperty(JSON_CHANNELS) Set<CampaignControllerTriggerShareChannel> channels,
        @JsonProperty(JSON_QUALITY) ShareQuality shareQuality,
        @JsonProperty(COMPONENT_REFERENCES) List<CampaignComponentReferenceConfiguration> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.SHARE, triggerPhase, name, description, enabled, negated,
            componentReferences);
        if (channels == null) {
            channels = shareTypes;
        }
        this.channels = channels != null ? channels : Collections.emptySet();
        this.quality = shareQuality;
    }

    @JsonProperty(JSON_CHANNELS)
    public Set<CampaignControllerTriggerShareChannel> getChannels() {
        return channels;
    }

    @JsonProperty(JSON_QUALITY)
    public ShareQuality getQuality() {
        return quality;
    }

}
