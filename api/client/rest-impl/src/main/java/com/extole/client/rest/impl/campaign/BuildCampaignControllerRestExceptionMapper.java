package com.extole.client.rest.impl.campaign;

import com.extole.client.rest.campaign.BuildCampaignControllerRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.controller.exception.BuildCampaignControllerTriggerCycleException;
import com.extole.model.service.campaign.controller.exception.BuildCampaignControllerTriggerDuplicateNameException;
import com.extole.model.service.campaign.controller.exception.BuildCampaignControllerTriggerParentGroupNotFoundException;
import com.extole.model.service.campaign.controller.exception.BuildCampaignControllerTriggerParentGroupPhaseMismatchException;
import com.extole.model.service.campaign.controller.exception.BuildCampaignControllerTriggerParentNotGroupException;

public final class BuildCampaignControllerRestExceptionMapper {

    private static final BuildCampaignControllerRestExceptionMapper INSTANCE =
        new BuildCampaignControllerRestExceptionMapper();

    public static BuildCampaignControllerRestExceptionMapper getInstance() {
        return INSTANCE;
    }

    private BuildCampaignControllerRestExceptionMapper() {
    }

    public BuildCampaignControllerRestException map(BuildCampaignException exception) {
        return map(exception, false);
    }

    public BuildCampaignControllerRestException map(BuildCampaignException exception, boolean isDeleteOperation) {
        if (exception instanceof BuildCampaignControllerTriggerCycleException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignControllerRestException.class)
                .withErrorCode(BuildCampaignControllerRestException.TRIGGER_CYCLE)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("controller_id", castedException.getEntityId())
                .addParameter("triggers_in_cycle", castedException.getTriggersInCycle())
                .withCause(exception)
                .build();
        }

        if (exception instanceof BuildCampaignControllerTriggerParentGroupPhaseMismatchException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignControllerRestException.class)
                .withErrorCode(BuildCampaignControllerRestException.NESTED_TRIGGERS_FOR_DISTINCT_PHASES)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("controller_id", castedException.getEntityId())
                .addParameter("parent_trigger_group_name", castedException.getParentTriggerGroupName())
                .addParameter("parent_trigger_phase", castedException.getParentTriggerGroupPhase())
                .addParameter("trigger_phase", castedException.getTriggerPhase())
                .withCause(exception)
                .build();
        }

        if (exception instanceof BuildCampaignControllerTriggerDuplicateNameException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignControllerRestException.class)
                .withErrorCode(BuildCampaignControllerRestException.TRIGGERS_WITH_DUPLICATE_NAME)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("controller_id", castedException.getEntityId())
                .addParameter("trigger_name", castedException.getTriggerName())
                .addParameter("other_trigger_id", castedException.getOtherTriggerId())
                .withCause(exception)
                .build();
        }

        if (exception instanceof BuildCampaignControllerTriggerParentGroupNotFoundException castedException
            && !isDeleteOperation) {
            return RestExceptionBuilder.newBuilder(BuildCampaignControllerRestException.class)
                .withErrorCode(BuildCampaignControllerRestException.PARENT_TRIGGER_GROUP_NAME_DOES_NOT_EXIST)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("controller_id", castedException.getEntityId())
                .addParameter("parent_trigger_group_name", castedException.getParentTriggerGroupName())
                .withCause(exception)
                .build();
        } else if (exception instanceof BuildCampaignControllerTriggerParentGroupNotFoundException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignControllerRestException.class)
                .withErrorCode(BuildCampaignControllerRestException.TRIGGER_GROUP_IS_NOT_EMPTY)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("controller_id", castedException.getEntityId())
                .withCause(exception)
                .build();
        }

        if (exception instanceof BuildCampaignControllerTriggerParentNotGroupException castedException) {
            return RestExceptionBuilder.newBuilder(BuildCampaignControllerRestException.class)
                .withErrorCode(BuildCampaignControllerRestException.PARENT_TRIGGER_GROUP_NAME_NOT_A_GROUP)
                .addParameter("campaign_id", exception.getCampaignId())
                .addParameter("controller_id", castedException.getEntityId())
                .addParameter("parent_trigger_group_name", castedException.getParentTriggerGroupName())
                .addParameter("parent_trigger_type", castedException.getParentTriggerType())
                .withCause(exception)
                .build();
        }

        return RestExceptionBuilder.newBuilder(BuildCampaignControllerRestException.class)
            .withErrorCode(BuildCampaignControllerRestException.CONTROLLER_BUILD_FAILED)
            .addParameter("campaign_id", exception.getCampaignId())
            .addParameter("controller_id", exception.getEntityId())
            .withCause(exception).build();
    }
}
