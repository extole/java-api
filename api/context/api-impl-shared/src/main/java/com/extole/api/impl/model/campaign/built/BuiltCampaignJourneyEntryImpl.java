package com.extole.api.impl.model.campaign.built;

import com.extole.api.model.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.api.model.campaign.built.BuiltCampaignJourneyEntry;

final class BuiltCampaignJourneyEntryImpl implements BuiltCampaignJourneyEntry {
    private final com.extole.model.entity.campaign.built.BuiltCampaignJourneyEntry campaignJourneyEntry;

    BuiltCampaignJourneyEntryImpl(
        com.extole.model.entity.campaign.built.BuiltCampaignJourneyEntry campaignJourneyEntry) {
        this.campaignJourneyEntry = campaignJourneyEntry;
    }

    @Override
    public String getJourneyName() {
        return campaignJourneyEntry.getJourneyName().getValue();
    }

    @Override
    public String getPriority() {
        return campaignJourneyEntry.getPriority().toString();
    }

    @Override
    public String getType() {
        return campaignJourneyEntry.getType().name();
    }

    @Override
    public String getId() {
        return campaignJourneyEntry.getId().getValue();
    }

    @Override
    public boolean isEnabled() {
        return campaignJourneyEntry.isEnabled();
    }

    @Override
    public BuiltCampaignControllerTrigger[] getTriggers() {
        return campaignJourneyEntry.getTriggers().stream()
            .map(value -> new BuiltCampaignControllerTriggerImpl(value))
            .toArray(BuiltCampaignControllerTrigger[]::new);
    }

    @Override
    public String getCreatedDate() {
        return campaignJourneyEntry.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return campaignJourneyEntry.getUpdatedDate().toString();
    }
}
