package com.extole.client.rest.impl.campaign.controller.action;

import java.time.ZoneId;
import java.util.Map;

import com.extole.client.rest.campaign.configuration.CampaignControllerActionConfiguration;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;
import com.extole.model.entity.campaign.CampaignControllerAction;
import com.extole.model.entity.campaign.CampaignControllerActionType;

public interface CampaignControllerActionResponseMapper<
    I extends CampaignControllerAction,
    O_RESPONSE extends CampaignControllerActionResponse,
    O_CONFIGURATION extends CampaignControllerActionConfiguration> {

    O_RESPONSE toResponse(I action, ZoneId timeZone);

    O_CONFIGURATION toConfiguration(I action, ZoneId timeZone, Map<Id<CampaignComponent>, String> componentNames);

    CampaignControllerActionType getActionType();

}
