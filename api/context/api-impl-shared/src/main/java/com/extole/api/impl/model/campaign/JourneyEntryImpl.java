package com.extole.api.impl.model.campaign;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.model.campaign.ControllerTrigger;
import com.extole.api.model.campaign.JourneyEntry;
import com.extole.dewey.decimal.DeweyDecimal;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;

public final class JourneyEntryImpl implements JourneyEntry {
    private final com.extole.model.entity.campaign.CampaignJourneyEntry campaignJourneyEntry;

    public JourneyEntryImpl(com.extole.model.entity.campaign.CampaignJourneyEntry campaignJourneyEntry) {
        this.campaignJourneyEntry = campaignJourneyEntry;
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getJourneyName() {
        return Evaluatables.remapClassToClass(campaignJourneyEntry.getJourneyName(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, DeweyDecimal> getPriority() {
        return campaignJourneyEntry.getPriority();
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
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled() {
        return campaignJourneyEntry.getEnabled();
    }

    @Override
    public ControllerTrigger[] getTriggers() {
        return campaignJourneyEntry.getTriggers().stream()
            .map(value -> new ControllerTriggerImpl(value))
            .toArray(ControllerTrigger[]::new);
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
