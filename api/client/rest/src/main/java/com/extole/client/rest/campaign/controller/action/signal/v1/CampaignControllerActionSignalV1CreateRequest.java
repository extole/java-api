package com.extole.client.rest.campaign.controller.action.signal.v1;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
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

public class CampaignControllerActionSignalV1CreateRequest extends ComponentElementRequest {
    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_SIGNAL_POLLING_ID = "signal_polling_id";
    private static final String JSON_DATA = "data";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<String> signalPollingId;
    private final Omissible<Map<String, String>> data;

    @JsonCreator
    private CampaignControllerActionSignalV1CreateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_SIGNAL_POLLING_ID) Omissible<String> signalPollingId,
        @JsonProperty(JSON_DATA) Omissible<Map<String, String>> data) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.signalPollingId = signalPollingId;
        this.data = data;
    }

    public CampaignControllerActionSignalV1CreateRequest(
        String signalPollingId, Map<String, String> data) {
        this(Omissible.omitted(),
            Omissible.of(Provided.booleanTrue()),
            Omissible.of(Collections.emptyList()),
            Omissible.of(Collections.emptyList()),
            Omissible.of(signalPollingId),
            Omissible.of(data));
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
    public Omissible<String> getSignalPollingId() {
        return signalPollingId;
    }

    @JsonProperty(JSON_DATA)
    public Omissible<Map<String, String>> getData() {
        return data;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ComponentElementRequest.Builder<Builder> {
        private Omissible<CampaignControllerActionQuality> quality = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled = Omissible.omitted();
        private Omissible<String> signalPollingId = Omissible.omitted();
        private Omissible<Map<String, String>> data = Omissible.omitted();

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

        public Builder withSignalPollingId(String signalPollingId) {
            this.signalPollingId = Omissible.of(signalPollingId);
            return this;
        }

        public Builder withData(Map<String, String> data) {
            this.data = Omissible.of(data);
            return this;
        }

        public CampaignControllerActionSignalV1CreateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionSignalV1CreateRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                signalPollingId,
                data);
        }
    }
}
