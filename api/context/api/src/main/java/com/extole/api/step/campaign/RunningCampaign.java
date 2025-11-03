package com.extole.api.step.campaign;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.step.campaign.step.Step;

@Schema
public interface RunningCampaign {

    enum CampaignState {
        LIVE, PAUSED, ENDED
    }

    String getId();

    String getState();

    String getProgramLabel();

    int getVersion();

    Step[] getSteps();

}
