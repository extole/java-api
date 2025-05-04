package com.extole.api.impl.model.campaign;

import com.extole.api.campaign.FlowStepBuildtimeContext;
import com.extole.api.model.campaign.FlowStepWords;
import com.extole.evaluateable.BuildtimeEvaluatable;

final class FlowStepWordsImpl implements FlowStepWords {
    private final com.extole.model.entity.campaign.CampaignFlowStepWords stepWords;

    FlowStepWordsImpl(com.extole.model.entity.campaign.CampaignFlowStepWords stepWords) {
        this.stepWords = stepWords;
    }

    @Override
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getSingularNounName() {
        return stepWords.getSingularNounName();
    }

    @Override
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getPluralNounName() {
        return stepWords.getPluralNounName();
    }

    @Override
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getVerbName() {
        return stepWords.getVerbName();
    }

    @Override
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getRateName() {
        return stepWords.getRateName();
    }

    @Override
    public BuildtimeEvaluatable<FlowStepBuildtimeContext, String> getPersonCountingName() {
        return stepWords.getPersonCountingName();
    }
}
