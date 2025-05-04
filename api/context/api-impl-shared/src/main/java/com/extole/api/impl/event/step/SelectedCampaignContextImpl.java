package com.extole.api.impl.event.step;

import java.util.stream.Collectors;

import com.extole.api.event.step.SelectedCampaignContext;
import com.extole.api.event.step.StepConsumerEventTriggerResult;
import com.extole.common.lang.ToString;
import com.extole.event.consumer.step.campaign.StepMatchingCampaignContext;

// TODO Add isFirstJourneyVisit() - ENG-22031
public final class SelectedCampaignContextImpl implements SelectedCampaignContext {

    private final String campaignId;
    private final Integer campaignVersion;
    private final String campaignState;
    private final String programLabel;
    private final String quality;
    private final StepConsumerEventTriggerResult[] triggerResults;
    private final String journeyName;

    public SelectedCampaignContextImpl(
        com.extole.event.consumer.step.campaign.SelectedCampaignContext selectedCampaignContext,
        StepMatchingCampaignContext selectedStepMatchingCampaignContext) {
        this.campaignId = selectedCampaignContext.getCampaignId().getValue();
        this.campaignVersion = selectedCampaignContext.getCampaignVersion();
        this.campaignState = selectedCampaignContext.getCampaignState().name();
        this.programLabel = selectedCampaignContext.getProgramLabel();
        this.quality = selectedCampaignContext.getQuality().name();
        this.triggerResults = selectedStepMatchingCampaignContext.getTriggerResults().stream()
            .map(value -> new com.extole.api.impl.event.step.StepConsumerEventTriggerResult(value))
            .collect(Collectors.toList())
            .toArray(new StepConsumerEventTriggerResult[] {});
        this.journeyName = selectedStepMatchingCampaignContext.getJourneyName().getValue();
    }

    @Override
    public String getCampaignId() {
        return campaignId;
    }

    @Override
    public Integer getCampaignVersion() {
        return campaignVersion;
    }

    @Override
    public String getCampaignState() {
        return campaignState;
    }

    @Override
    public String getProgramLabel() {
        return programLabel;
    }

    @Override
    public String getQuality() {
        return quality;
    }

    @Override
    public com.extole.api.event.step.StepConsumerEventTriggerResult[] getTriggerResults() {
        return triggerResults;
    }

    @Override
    public String getJourneyName() {
        return journeyName;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
