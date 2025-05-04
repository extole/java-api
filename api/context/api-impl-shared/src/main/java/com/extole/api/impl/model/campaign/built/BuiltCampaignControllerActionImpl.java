package com.extole.api.impl.model.campaign.built;

import com.extole.api.model.campaign.built.BuiltCampaignControllerAction;

final class BuiltCampaignControllerActionImpl implements BuiltCampaignControllerAction {
    private final com.extole.model.entity.campaign.built.BuiltCampaignControllerAction campaignControllerAction;

    BuiltCampaignControllerActionImpl(
        com.extole.model.entity.campaign.built.BuiltCampaignControllerAction campaignControllerAction) {
        this.campaignControllerAction = campaignControllerAction;
    }

    @Override
    public String getId() {
        return campaignControllerAction.getId().getValue();
    }

    @Override
    public String getType() {
        return campaignControllerAction.getType().name();
    }

    @Override
    public String getQuality() {
        return campaignControllerAction.getQuality().name();
    }

    @Override
    public Boolean getEnabled() {
        return campaignControllerAction.getEnabled();
    }

    @Override
    public String getCreatedDate() {
        return campaignControllerAction.getCreatedDate().toString();
    }
}
