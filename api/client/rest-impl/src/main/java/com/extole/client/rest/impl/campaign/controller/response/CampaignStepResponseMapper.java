package com.extole.client.rest.impl.campaign.controller.response;

import java.time.ZoneId;
import java.util.Map;

import com.extole.client.rest.campaign.configuration.CampaignStepConfiguration;
import com.extole.client.rest.campaign.controller.response.CampaignStepResponse;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.StepType;

public interface CampaignStepResponseMapper<ENTITY extends CampaignStep, RESPONSE extends CampaignStepResponse, CONFIGURATION extends CampaignStepConfiguration> {

    StepType getStepType();

    RESPONSE toResponse(ENTITY step, ZoneId timeZone);

    CONFIGURATION toConfiguration(ENTITY step, ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames);

}
