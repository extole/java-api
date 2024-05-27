package com.extole.api.event.step;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface SelectedCampaignContext {

    String getCampaignId();

    Integer getCampaignVersion();

    String getCampaignState();

    String getProgramLabel();

    String getQuality();

    StepConsumerEventTriggerResult[] getTriggerResults();

    String getJourneyName();

}
