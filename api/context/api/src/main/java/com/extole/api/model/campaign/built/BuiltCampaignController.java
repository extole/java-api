package com.extole.api.model.campaign.built;

public interface BuiltCampaignController extends BuiltCampaignStep {

    String getName();

    String getScope();

    String[] getEnabledOnStates();

    BuiltCampaignControllerAction[] getActions();

    String[] getSelectors();

    String[] getAliases();

    BuiltStepData[] getData();

    String[] getJourneyNames();

}
