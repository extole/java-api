package com.extole.client.rest.impl.campaign.built.controller.action;

import java.time.ZoneId;

import com.extole.client.rest.campaign.built.controller.action.BuiltCampaignControllerActionResponse;
import com.extole.model.entity.campaign.CampaignControllerActionType;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerAction;

public interface BuiltCampaignControllerActionResponseMapper<
    I extends BuiltCampaignControllerAction,
    O extends BuiltCampaignControllerActionResponse> {

    O toResponse(I action, ZoneId timeZone);

    CampaignControllerActionType getActionType();

}
