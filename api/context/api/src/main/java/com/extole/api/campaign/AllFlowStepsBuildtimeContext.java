package com.extole.api.campaign;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface AllFlowStepsBuildtimeContext extends FlowStepBuildtimeContext, FlowStep {

    String getSingularNounName();

    String getPluralNounName();

    String getVerbName();

    String getPersonCountingName();

    String getRateName();

    @Nullable
    FlowStep getPreviousStep();

    @Nullable
    FlowStep getJourneyStartStep();

    /**
     * FlowStep by step_name mapping
     */
    Map<String, FlowStep> getSteps();

}
