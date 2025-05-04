package com.extole.client.rest.impl.campaign.controller.update;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.update.CampaignStepUpdateRequest;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

public interface CampaignStepUpdateRequestMapper<REQUEST extends CampaignStepUpdateRequest, STEP extends CampaignStep> {

    StepType getStepType();

    STEP update(Authorization authorization, CampaignBuilder campaignBuilder, STEP step, REQUEST updateRequest)
        throws CampaignComponentValidationRestException, ConcurrentCampaignUpdateException,
        StaleCampaignVersionException, BuildCampaignException, InvalidComponentReferenceException,
        CampaignStepBuildException;

}
