package com.extole.api.impl.step;

import com.extole.api.step.Campaign;
import com.extole.running.service.campaign.RunningCampaign;

public class CampaignImpl implements Campaign {

    private final RunningCampaign campaign;

    public CampaignImpl(RunningCampaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public String getState() {
        return campaign.getState().name();
    }

}
