package com.extole.client.rest.impl.campaign.controller.trigger;

import java.time.ZoneId;
import java.util.Map;

import com.extole.client.rest.campaign.configuration.CampaignControllerTriggerConfiguration;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerTrigger;
import com.extole.model.entity.campaign.CampaignControllerTriggerType;

public interface CampaignControllerTriggerResponseMapper<I extends CampaignControllerTrigger, O_RESPONSE extends CampaignControllerTriggerResponse, O_CONFIGURATION extends CampaignControllerTriggerConfiguration> {

    O_RESPONSE toResponse(I trigger, ZoneId timeZone);

    O_CONFIGURATION toConfiguration(I trigger, ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames);

    CampaignControllerTriggerType getTriggerType();

}
