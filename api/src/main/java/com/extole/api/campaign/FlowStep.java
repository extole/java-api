package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface FlowStep {

    String getName();

    String getStepName();

    String getSingularNounName();

    String getPluralNounName();

    String getVerbName();

    String getPersonCountingName();

    String getRateName();

    @Nullable
    FlowStep getPreviousStep();

    @Nullable
    FlowStep getJourneyStartStep();

}
