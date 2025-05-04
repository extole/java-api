package com.extole.client.rest.impl.campaign.built.controller;

import java.time.ZoneId;

import com.extole.client.rest.campaign.built.controller.BuiltCampaignStepResponse;
import com.extole.model.entity.campaign.StepType;
import com.extole.model.entity.campaign.built.BuiltCampaignStep;

public interface BuiltCampaignStepResponseMapper<
    ENTITY extends BuiltCampaignStep,
    RESPONSE extends BuiltCampaignStepResponse> {

    StepType getStepType();

    RESPONSE toResponse(ENTITY step, ZoneId timeZone);

}
