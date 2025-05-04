package com.extole.client.rest.campaign.controller.action.display;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.step.action.display.ApiResponse;
import com.extole.api.step.action.display.DisplayActionContext;
import com.extole.api.step.action.display.DisplayActionResponseContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionQuality;
import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.RuntimeEvaluatable;
import com.extole.id.Id;

public final class CampaignControllerActionDisplayUpdateRequest extends ComponentElementRequest {

    private static final String JSON_QUALITY = "quality";
    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_BODY = "body";
    private static final String JSON_HEADERS = "headers";
    private static final String JSON_RESPONSE = "response";

    private final Omissible<CampaignControllerActionQuality> quality;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, String>>> body;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, Map<String, String>>>> headers;
    private final Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>>> response;

    @JsonCreator
    private CampaignControllerActionDisplayUpdateRequest(
        @JsonProperty(JSON_QUALITY) Omissible<CampaignControllerActionQuality> quality,
        @JsonProperty(JSON_ENABLED) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> enabled,
        @JsonProperty(JSON_COMPONENT_IDS) Omissible<List<Id<ComponentResponse>>> componentIds,
        @JsonProperty(JSON_COMPONENT_REFERENCES) Omissible<List<ComponentReferenceRequest>> componentReferences,
        @JsonProperty(JSON_BODY) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, String>>> body,
        @JsonProperty(JSON_HEADERS) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, Map<String, String>>>> headers,
        @JsonProperty(JSON_RESPONSE) Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>>> response) {
        super(componentReferences, componentIds);
        this.quality = quality;
        this.enabled = enabled;
        this.body = body;
        this.headers = headers;
        this.response = response;
    }

    @JsonProperty(JSON_QUALITY)
    public Omissible<CampaignControllerActionQuality> getQuality() {
        return quality;
    }

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    @JsonProperty(JSON_BODY)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, String>>> getBody() {
        return body;
    }

    @JsonProperty(JSON_HEADERS)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionContext, Map<String, String>>>> getHeaders() {
        return headers;
    }

    @JsonProperty(JSON_RESPONSE)
    public Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
        RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>>> getResponse() {
        return response;
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

        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, String>>> body = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, Map<String, String>>>> headers = Omissible.omitted();
        private Omissible<BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>>> response = Omissible.omitted();

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

        public Builder withBody(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, String>> body) {
            this.body = Omissible.of(body);
            return this;
        }

        public Builder withHeaders(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionContext, Map<String, String>>> headers) {
            this.headers = Omissible.of(headers);
            return this;
        }

        public Builder withResponse(BuildtimeEvaluatable<ControllerBuildtimeContext,
            RuntimeEvaluatable<DisplayActionResponseContext, ApiResponse>> response) {
            this.response = Omissible.of(response);
            return this;
        }

        public CampaignControllerActionDisplayUpdateRequest build() {
            Omissible<List<ComponentReferenceRequest>> componentReferences;
            if (componentReferenceBuilders.isEmpty()) {
                componentReferences = Omissible.omitted();
            } else {
                componentReferences = Omissible.of(componentReferenceBuilders.stream()
                    .map(builder -> builder.build())
                    .collect(Collectors.toList()));
            }

            return new CampaignControllerActionDisplayUpdateRequest(quality,
                enabled,
                componentIds,
                componentReferences,
                body,
                headers,
                response);
        }

    }

}
