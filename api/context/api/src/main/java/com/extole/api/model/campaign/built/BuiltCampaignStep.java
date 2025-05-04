package com.extole.api.model.campaign.built;

public interface BuiltCampaignStep {

    String getType();

    String getId();

    boolean isEnabled();

    BuiltCampaignControllerTrigger[] getTriggers();

    String getCreatedDate();

    String getUpdatedDate();

}
