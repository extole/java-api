package com.extole.client.rest.impl.campaign;

import static com.extole.client.rest.campaign.BuildCampaignRestException.CAMPAIGN_BUILD_FAILED;
import static com.extole.client.rest.campaign.BuildCampaignRestException.REBUILD_CAMPAIGNS_BUILD_FAILED;

import org.apache.commons.lang3.StringUtils;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.BuildCampaignEvaluatableException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignRebuildFailedCampaignsException;
import com.extole.model.service.campaign.controller.exception.CampaignFrontendControllerStateMisconfigurationException;

public final class BuildCampaignRestExceptionMapper {

    private static final BuildCampaignRestExceptionMapper INSTANCE = new BuildCampaignRestExceptionMapper();

    public static BuildCampaignRestExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildCampaignRestExceptionMapper() {
    }

    public BuildCampaignRestException map(BuildCampaignException exception) {

        // TODO handle this with a dedicated exception in ENG-23826
        if (exception instanceof CampaignFrontendControllerStateMisconfigurationException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(BuildCampaignRestException.BUILT_FRONTEND_CONTROLLER_STATE_MISCONFIGURATION)
                .addParameter("entity", exception.getEntity())
                .addParameter("controller_id", castedException.getControllerId())
                .addParameter("details", castedException.getMisconfigurationDetails())
                .withCause(exception)
                .build();
        }
        if (exception instanceof BuildCampaignEvaluatableException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(CAMPAIGN_BUILD_FAILED)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("campaign_version", exception.getCampaignVersion())
                .addParameter("entity", exception.getEntity())
                .addParameter("entity_id", exception.getEntityId())
                .addParameter("evaluatable_name", castedException.getEvaluatableName())
                .addParameter("evaluatable", castedException.getEvaluatable().toString())
                .withCause(exception)
                .build();
        }
        if (exception instanceof CampaignRebuildFailedCampaignsException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
                .withErrorCode(REBUILD_CAMPAIGNS_BUILD_FAILED)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("campaign_version", exception.getCampaignVersion())
                .addParameter("entity", exception.getEntity())
                .addParameter("entity_id", exception.getEntityId())
                .addParameter("failed_campaigns", castedException.getFailedCampaigns())
                .withCause(castedException)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildCampaignRestException.class)
            .withErrorCode(CAMPAIGN_BUILD_FAILED)
            .addParameter("campaign_id", exception.getCampaignId())
            .addParameter("campaign_version", exception.getCampaignVersion())
            .addParameter("entity", exception.getEntity())
            .addParameter("entity_id", exception.getEntityId())
            .addParameter("evaluatable_name", StringUtils.EMPTY)
            .addParameter("evaluatable", StringUtils.EMPTY)
            .withCause(exception).build();
    }
}
