package com.extole.api.impl.model.campaign.built;

import com.extole.api.model.campaign.built.BuiltCampaignLabel;

final class BuiltCampaignLabelImpl implements BuiltCampaignLabel {
    private final com.extole.model.entity.campaign.built.BuiltCampaignLabel campaignLabel;

    BuiltCampaignLabelImpl(com.extole.model.entity.campaign.built.BuiltCampaignLabel campaignLabel) {
        this.campaignLabel = campaignLabel;
    }

    @Override
    public String getName() {
        return campaignLabel.getName();
    }

    @Override
    public String getType() {
        return campaignLabel.getType().name();
    }

    @Override
    public String getCreatedDate() {
        return campaignLabel.getCreatedDate().toString();
    }
}
