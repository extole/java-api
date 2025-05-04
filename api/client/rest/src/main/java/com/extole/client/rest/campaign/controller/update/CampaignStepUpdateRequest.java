package com.extole.client.rest.campaign.controller.update;

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
    property = CampaignStepUpdateRequest.JSON_TYPE)
@JsonSubTypes({
    @Type(value = CampaignFrontendControllerUpdateRequest.class,
        name = CampaignFrontendControllerUpdateRequest.STEP_TYPE_FRONTEND_CONTROLLER),
    @Type(value = CampaignControllerUpdateRequest.class,
        name = CampaignControllerUpdateRequest.STEP_TYPE_CONTROLLER),
    @Type(value = CampaignJourneyEntryUpdateRequest.class,
        name = CampaignJourneyEntryUpdateRequest.STEP_TYPE_JOURNEY_ENTRY)
})

@Schema(discriminatorProperty = CampaignStepUpdateRequest.JSON_TYPE, discriminatorMapping = {
    @DiscriminatorMapping(value = CampaignFrontendControllerUpdateRequest.STEP_TYPE_FRONTEND_CONTROLLER,
        schema = CampaignFrontendControllerUpdateRequest.class),
    @DiscriminatorMapping(value = CampaignControllerUpdateRequest.STEP_TYPE_CONTROLLER,
        schema = CampaignControllerUpdateRequest.class),
    @DiscriminatorMapping(value = CampaignJourneyEntryUpdateRequest.STEP_TYPE_JOURNEY_ENTRY,
        schema = CampaignJourneyEntryUpdateRequest.class)
})
public abstract class CampaignStepUpdateRequest extends ComponentElementRequest {

    static final String JSON_TYPE = "type";
    static final String JSON_ENABLED = "enabled";

    private final Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled;

    protected CampaignStepUpdateRequest(
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

    public abstract static class Builder<
        BUILDER extends Builder<BUILDER, REQUEST>,
        REQUEST extends CampaignStepUpdateRequest> extends ComponentElementRequest.Builder<BUILDER> {

        protected Omissible<BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean>> enabled = Omissible.omitted();

        public BUILDER withEnabled(BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> enabled) {
            this.enabled = Omissible.of(enabled);
            return (BUILDER) this;
        }

        public abstract REQUEST build();

    }

}
