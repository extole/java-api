package com.extole.api.step;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Campaign {

    enum CampaignState {
        LIVE, PAUSED, ENDED
    }

    String getState();

}
