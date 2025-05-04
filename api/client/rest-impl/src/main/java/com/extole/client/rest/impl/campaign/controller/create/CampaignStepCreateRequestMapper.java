package com.extole.client.rest.impl.campaign.controller.create;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.client.rest.campaign.controller.StepType;
import com.extole.client.rest.campaign.controller.create.CampaignStepCreateRequest;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.InvalidComponentReferenceException;
import com.extole.model.service.campaign.BuildCampaignException;
import com.extole.model.service.campaign.CampaignBuilder;
import com.extole.model.service.campaign.ConcurrentCampaignUpdateException;
import com.extole.model.service.campaign.StaleCampaignVersionException;
import com.extole.model.service.campaign.step.CampaignStepBuildException;

public interface CampaignStepCreateRequestMapper<REQUEST extends CampaignStepCreateRequest, STEP extends CampaignStep> {

    StepType getStepType();

    STEP create(Authorization authorization, CampaignBuilder campaignBuilder, REQUEST createRequest)
        throws CampaignComponentValidationRestException, ConcurrentCampaignUpdateException,
        StaleCampaignVersionException, InvalidComponentReferenceException, CampaignStepBuildException,
        BuildCampaignException;

}
