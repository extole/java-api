package com.extole.client.rest.impl.campaign;

import static com.extole.client.rest.campaign.BuildCampaignRestException.CAMPAIGN_BUILD_FAILED;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerStateMisconfigurationException;

public final class BuildCampaignRestExceptionMapper {

    private static final BuildCampaignRestExceptionMapper INSTANCE = new BuildCampaignRestExceptionMapper();

    public static BuildCampaignRestExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildCampaignRestExceptionMapper() {
    }

    public BuildCampaignRestException map(BuildCampaignException e) {

        // TODO handle this with a dedicated exception in ENG-23826
        if (e instanceof CampaignFrontendControllerStateMisconfigurationException) {
            CampaignFrontendControllerStateMisconfigurationException castedException =
                (CampaignFrontendControllerStateMisconfigurationException) e;
            return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(BuildCampaignRestException.BUILT_FRONTEND_CONTROLLER_STATE_MISCONFIGURATION)
                .addParameter("entity", e.getEntity())
                .addParameter("controller_id", castedException.getControllerId())
                .addParameter("details", castedException.getMisconfigurationDetails())
                .withCause(e)
                .build();
        }

        if (e instanceof BuildCampaignEvaluatableException) {
            BuildCampaignEvaluatableException castedException = (BuildCampaignEvaluatableException) e;
            return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(CAMPAIGN_BUILD_FAILED)
                .addParameter("campaign_id", e.getCampaignId())
                .addParameter("campaign_version", e.getCampaignVersion())
                .addParameter("entity", e.getEntity())
                .addParameter("entity_id", e.getEntityId())
                .addParameter("evaluatable_name", castedException.getEvaluatableName())
                .addParameter("evaluatable", castedException.getEvaluatable().toString())
                .withCause(e)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
            .withErrorCode(CAMPAIGN_BUILD_FAILED)
            .addParameter("campaign_id", e.getCampaignId())
            .addParameter("campaign_version", e.getCampaignVersion())
            .addParameter("entity", e.getEntity())
            .addParameter("entity_id", e.getEntityId())
            .withCause(e).build();
    }
}
