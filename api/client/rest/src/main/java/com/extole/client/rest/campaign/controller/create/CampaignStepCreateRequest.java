package com.extole.client.rest.campaign.controller.create;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.client.rest.campaign.component.ComponentElementRequest;
import com.extole.client.rest.campaign.component.ComponentReferenceRequest;
import com.extole.client.rest.campaign.component.ComponentResponse;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.common.rest.omissible.Omissible;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.id.Id;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = CampaignStepCreateRequest.JSON_TYPE)
@JsonSubTypes({
    @Type(value = CampaignFrontendControllerCreateRequest.class,
        name = CampaignFrontendControllerCreateRequest.STEP_TYPE_FRONTEND_CONTROLLER),
    @Type(value = CampaignControllerCreateRequest.class,
        name = CampaignControllerCreateRequest.STEP_TYPE_CONTROLLER),
    @Type(value = CampaignJourneyEntryCreateRequest.class,
        name = CampaignJourneyEntryCreateRequest.STEP_TYPE_JOURNEY_ENTRY)
})

@Schema(discriminatorProperty = CampaignStepCreateRequest.JSON_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = CampaignFrontendControllerCreateRequest.STEP_TYPE_FRONTEND_CONTROLLER,
        schema = CampaignFrontendControllerCreateRequest.class),
    @DiscriminatorMapping(value = CampaignControllerCreateRequest.STEP_TYPE_CONTROLLER,
        schema = CampaignControllerCreateRequest.class),
    @DiscriminatorMapping(value = CampaignJourneyEntryCreateRequest.STEP_TYPE_JOURNEY_ENTRY,
        schema = CampaignJourneyEntryCreateRequest.class)
})
public abstract class CampaignStepCreateRequest extends ComponentElementRequest {

    static final String JSON_TYPE = "type";
    static final String JSON_ENABLED = "enabled";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled;

    protected CampaignStepCreateRequest(
        Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled,
        Omissible<List<Id<ComponentResponse>>> componentIds,
        Omissible<List<ComponentReferenceRequest>> componentReferences) {
        super(componentReferences, componentIds);
        this.enabled = enabled;
    }

    public abstract StepType getType();

    @JsonProperty(JSON_ENABLED)
    public Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> getEnabled() {
        return enabled;
    }

    public abstract static class Builder<BUILDER extends Builder<BUILDER,
        REQUEST>, REQUEST extends CampaignStepCreateRequest>
        extends ComponentElementRequest.Builder<BUILDER> {

        protected Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled = Omissible.omitted();

        public BUILDER withEnabled(BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return (BUILDER) this;
        }

        public abstract REQUEST build();

    }

}
