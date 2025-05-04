package com.extole.client.rest.campaign.built.controller.trigger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.client.rest.campaign.component.ComponentReferenceResponse;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerType;
import com.extole.client.rest.campaign.controller.trigger.share.CampaignControllerTriggerShareChannel;
import com.extole.client.rest.campaign.controller.trigger.share.ShareQuality;
import com.extole.id.Id;

public class BuiltCampaignControllerTriggerShareResponse extends BuiltCampaignControllerTriggerResponse {
    @Deprecated // TODO remove share_types ENG-10463
    private static final String JSON_SHARE_TYPES = "share_types";
    private static final String JSON_CHANNELS = "channels";
    private static final String JSON_QUALITY = "quality";

    private final Set<CampaignControllerTriggerShareChannel> channels;
    private final ShareQuality quality;

    public BuiltCampaignControllerTriggerShareResponse(
        @JsonProperty(TRIGGER_ID) String triggerId,
        @JsonProperty(TRIGGER_PHASE) CampaignControllerTriggerPhase triggerPhase,
        @JsonProperty(TRIGGER_NAME) String name,
        @JsonProperty(TRIGGER_DESCRIPTION) Optional<String> description,
        @JsonProperty(ENABLED) Boolean enabled,
        @JsonProperty(NEGATED) Boolean negated,
        @JsonProperty(JSON_SHARE_TYPES) Set<CampaignControllerTriggerShareChannel> shareTypes,
        @JsonProperty(JSON_CHANNELS) Set<CampaignControllerTriggerShareChannel> channels,
        @JsonProperty(JSON_QUALITY) ShareQuality shareQuality,
        @JsonProperty(JSON_COMPONENT_IDS) List<Id<ComponentResponse>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) List<ComponentReferenceResponse> componentReferences) {
        super(triggerId, CampaignControllerTriggerType.SHARE, triggerPhase, name, description, enabled,
            negated, componentIds, componentReferences);
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
