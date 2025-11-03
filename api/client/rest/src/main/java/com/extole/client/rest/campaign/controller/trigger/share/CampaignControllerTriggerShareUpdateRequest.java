package com.extole.client.rest.campaign.controller.trigger.share;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerRequest;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerShareUpdateRequest extends CampaignControllerTriggerRequest {

    @Deprecated // TODO rename to channels ENG-10463
    private static final String SHARE_TYPES = "share_types";
    private static final String CHANNELS = "channels";
    private static final String QUALITY = "quality";

    private final Set<CampaignControllerTriggerShareChannel> channels;
    private final ShareQuality quality;

    public CampaignControllerTriggerShareUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(PARENT_TRIGGER_GROUP_NAME) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> parentTriggerGroupName,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(SHARE_TYPES) Set<CampaignControllerTriggerShareChannel> shareTypes,
        @JsonProperty(CHANNELS) Set<CampaignControllerTriggerShareChannel> channels,
        @JsonProperty(QUALITY) ShareQuality quality,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(triggerPhase, name, parentTriggerGroupName, description, enabled, negated, componentIds,
            componentReferences);
        if (channels == null) {
            channels = shareTypes;
        }
        this.channels = channels != null ? channels : Collections.emptySet();
        this.quality = quality;
    }

    @Nullable
    @JsonProperty(CHANNELS)
    public Set<CampaignControllerTriggerShareChannel> getChannels() {
        return channels;
    }

    @JsonProperty(QUALITY)
    public ShareQuality getQuality() {
        return quality;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends CampaignControllerTriggerRequest.Builder<Builder> {

        private Set<CampaignControllerTriggerShareChannel> channels;
        private ShareQuality quality;

        private Builder() {
        }

        public Builder withChannels(Set<CampaignControllerTriggerShareChannel> channels) {
            this.channels = channels;
            return this;
        }

        public Builder withQuality(ShareQuality quality) {
            this.quality = quality;
            return this;
        }

        public CampaignControllerTriggerShareUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerTriggerShareUpdateRequest(triggerPhase,
                name,
                parentTriggerGroupName,
                description,
                enabled,
                negated,
                channels,
                channels,
                quality,
                componentIds,
                componentReferences);
        }
    }

}
