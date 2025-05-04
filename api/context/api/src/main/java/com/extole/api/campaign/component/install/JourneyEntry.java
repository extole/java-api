package com.extole.api.campaign.component.install;

public interface JourneyEntry {

    String getId();

    String getJourneyName();

    void anchor(Trigger trigger);

}
