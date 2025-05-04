package com.extole.api.impl.model.campaign;

import com.extole.api.model.campaign.Label;

public final class LabelImpl implements Label {
    private final com.extole.model.entity.campaign.CampaignLabel campaignLabel;

    public LabelImpl(com.extole.model.entity.campaign.CampaignLabel campaignLabel) {
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
