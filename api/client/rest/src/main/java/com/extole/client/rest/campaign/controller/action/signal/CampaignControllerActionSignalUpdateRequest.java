package com.extole.client.rest.campaign.controller.action.signal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.signal.SignalActionContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public class CampaignControllerActionSignalUpdateRequest extends ComponentElementRequest {
    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_SIGNAL_POLLING_ID = "signal_polling_id";
    private static final String JSON_NAME = "name";
    private static final String JSON_DATA = "data";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<RuntimeEvaluatable<SignalActionContext, String>> signalPollingId;
    private final Omissible<RuntimeEvaluatable<SignalActionContext, String>> name;
    private final Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<SignalActionContext, Optional<Object>>>>> data;

    @JsonCreator
    public CampaignControllerActionSignalUpdateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_SIGNAL_POLLING_ID) Omissible<
            RuntimeEvaluatable<SignalActionContext, String>> signalPollingId,
        @JsonProperty(JSON_NAME) Omissible<RuntimeEvaluatable<SignalActionContext, String>> name,
        @JsonProperty(JSON_DATA) Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<SignalActionContext, Optional<Object>>>>> data) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.signalPollingId = signalPollingId;
        this.name = name;
        this.data = data;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_SIGNAL_POLLING_ID)
    public Omissible<RuntimeEvaluatable<SignalActionContext, String>> getSignalPollingId() {
        return signalPollingId;
    }

    @JsonProperty(JSON_NAME)
    public Omissible<RuntimeEvaluatable<SignalActionContext, String>> getName() {
        return name;
    }

    @JsonProperty(JSON_DATA)
    public Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<SignalActionContext, Optional<Object>>>>> getData() {
        return data;
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
        private Omissible<RuntimeEvaluatable<SignalActionContext, String>> signalPollingId = Omissible.omitted();
        private Omissible<RuntimeEvaluatable<SignalActionContext, String>> name = Omissible.omitted();
        private Omissible<Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<SignalActionContext, Optional<Object>>>>> data = Omissible.omitted();

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

        public Builder withSignalPollingId(RuntimeEvaluatable<SignalActionContext, String> signalPollingId) {
            this.signalPollingId = Omissible.of(signalPollingId);
            return this;
        }

        public Builder withName(RuntimeEvaluatable<SignalActionContext, String> name) {
            this.name = Omissible.of(name);
            return this;
        }

        public Builder withData(Map<String, BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<SignalActionContext, Optional<Object>>>> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public CampaignControllerActionSignalUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionSignalUpdateRequest(
                quality,
                enabled,
                componentIds,
                componentReferences,
                signalPollingId,
                name,
                data);
        }
    }
}
