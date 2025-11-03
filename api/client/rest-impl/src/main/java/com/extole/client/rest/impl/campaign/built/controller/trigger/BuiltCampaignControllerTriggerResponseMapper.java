package com.extole.client.rest.impl.campaign.built.controller.trigger;

import java.time.ZoneId;

import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;

public interface BuiltCampaignControllerTriggerResponseMapper<I extends BuiltCampaignControllerTrigger, O extends BuiltCampaignControllerTriggerResponse> {

    O toResponse(I trigger, ZoneId timeZone);

    CampaignControllerTriggerType getTriggerType();

}
