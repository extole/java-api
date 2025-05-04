package com.extole.api.impl.model.campaign.built;

import com.extole.api.model.campaign.built.BuiltCampaignFlowStepWords;

final class BuiltCampaignFlowStepWordsImpl implements BuiltCampaignFlowStepWords {
    private final com.extole.model.entity.campaign.built.BuiltCampaignFlowStepWords flowStepWords;

    BuiltCampaignFlowStepWordsImpl(com.extole.model.entity.campaign.built.BuiltCampaignFlowStepWords flowStepWords) {
        this.flowStepWords = flowStepWords;
    }

    @Override
    public String getSingularNounName() {
        return flowStepWords.getSingularNounName();
    }

    @Override
    public String getPluralNounName() {
        return flowStepWords.getPluralNounName();
    }

    @Override
    public String getVerbName() {
        return flowStepWords.getVerbName();
    }

    @Override
    public String getRateName() {
        return flowStepWords.getRateName();
    }

    @Override
    public String getPersonCountingName() {
        return flowStepWords.getPersonCountingName();
    }
}
