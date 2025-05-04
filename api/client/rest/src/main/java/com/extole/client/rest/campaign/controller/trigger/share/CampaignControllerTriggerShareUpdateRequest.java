package com.extole.client.rest.campaign.controller.trigger.share;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerPhase;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerTriggerShareUpdateRequest extends ComponentElementRequest {
    private static final String TRIGGER_PHASE = "trigger_phase";
    private static final String TRIGGER_NAME = "trigger_name";
    private static final String TRIGGER_DESCRIPTION = "trigger_description";
    private static final String ENABLED = "enabled";
    private static final String NEGATED = "negated";
    @Deprecated // TODO rename to channels ENG-10463
    private static final String SHARE_TYPES = "share_types";
    private static final String CHANNELS = "channels";
    private static final String QUALITY = "quality";

    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        CampaignControllerTriggerPhase>> triggerPhase;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated;
    private final Set<CampaignControllerTriggerShareChannel> channels;
    private final ShareQuality quality;

    public CampaignControllerTriggerShareUpdateRequest(
        @JsonProperty(TRIGGER_PHASE) Omissible<
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>> triggerPhase,
        @JsonProperty(TRIGGER_NAME) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name,
        @JsonProperty(TRIGGER_DESCRIPTION) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            Optional<String>>> description,
        @JsonProperty(ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(NEGATED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated,
        @JsonProperty(SHARE_TYPES) Set<CampaignControllerTriggerShareChannel> shareTypes,
        @JsonProperty(CHANNELS) Set<CampaignControllerTriggerShareChannel> channels,
        @JsonProperty(QUALITY) ShareQuality quality,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.triggerPhase = triggerPhase;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.negated = negated;
        if (channels == null) {
            channels = shareTypes;
        }
        this.channels = channels != null ? channels : Collections.emptySet();
        this.quality = quality;
    }

    @JsonProperty(TRIGGER_PHASE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase>>
        getTriggerPhase() {
        return triggerPhase;
    }

    @JsonProperty(TRIGGER_NAME)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> getName() {
        return name;
    }

    @JsonProperty(TRIGGER_DESCRIPTION)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> getDescription() {
        return description;
    }

    @JsonProperty(ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(NEGATED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getNegated() {
        return negated;
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

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            CampaignControllerTriggerPhase>> triggerPhase = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, String>> name = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>>> description =
            Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> negated = Omissible.omitted();
        private Set<CampaignControllerTriggerShareChannel> channels;
        private ShareQuality quality;

        private Builder() {
        }

        public Builder withTriggerPhase(
            BuildtimeEvaluatable<ControllerBuildtimeContext, CampaignControllerTriggerPhase> triggerPhase) {
            this.triggerPhase = Omissible.of(triggerPhase);
            return this;
        }

        public Builder withName(BuildtimeEvaluatable<ControllerBuildtimeContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withDescription(BuildtimeEvaluatable<ControllerBuildtimeContext, Optional<String>> description) {
            this.description = Omissible.of(description);
            return this;
        }

        public Builder withEnabled(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return this;
        }

        public Builder withNegated(BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> negated) {
            this.negated = Omissible.of(negated);
            return this;
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
