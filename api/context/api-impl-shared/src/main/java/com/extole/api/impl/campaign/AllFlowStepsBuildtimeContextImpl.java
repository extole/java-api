package com.extole.api.impl.campaign;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.extole.api.campaign.AllFlowStepsBuildtimeContext;
import com.extole.api.campaign.FlowStep;
import com.extole.api.campaign.FlowStepBuildtimeContext;

public class AllFlowStepsBuildtimeContextImpl extends ExtendableCampaignBuildtimeContextImpl
    implements AllFlowStepsBuildtimeContext {
    private final FlowStep flowStep;
    private final Map<String, FlowStep> relatedFlowStepByStepName;
    private final Function<FlowStep, FlowStep> journeyStartStepFinder;
    private final Function<FlowStep, Optional<FlowStep>> previousStepFinder;

    public AllFlowStepsBuildtimeContextImpl(FlowStep flowStep,
        FlowStepBuildtimeContext context,
        Map<String, FlowStep> relatedFlowStepByStepName,
        Function<FlowStep, FlowStep> journeyStartStepFinder,
        Function<FlowStep, Optional<FlowStep>> previousStepFinder) {
        super(context);
        this.flowStep = flowStep;
        this.relatedFlowStepByStepName = relatedFlowStepByStepName;
        this.journeyStartStepFinder = journeyStartStepFinder;
        this.previousStepFinder = previousStepFinder;
    }

    @Override
    public String getStepName() {
        return flowStep.getStepName();
    }

    @Override
    public String getName() {
        return flowStep.getName();
    }

    @Override
    public String getSingularNounName() {
        return flowStep.getSingularNounName();
    }

    @Override
    public String getPluralNounName() {
        return flowStep.getPluralNounName();
    }

    @Override
    public String getVerbName() {
        return flowStep.getVerbName();
    }

    @Override
    public String getPersonCountingName() {
        return flowStep.getPersonCountingName();
    }

    @Override
    public String getRateName() {
        return flowStep.getRateName();
    }

    @Override
    @Nullable
    public FlowStep getPreviousStep() {
        return previousStepFinder.apply(flowStep).orElse(null);
    }

    @Override
    @Nullable
    public FlowStep getJourneyStartStep() {
        return journeyStartStepFinder.apply(flowStep);
    }

    @Override
    public Map<String, FlowStep> getSteps() {
        return relatedFlowStepByStepName;
    }

}
