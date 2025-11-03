package com.extole.api.step.data;

import javax.annotation.Nullable;

import com.extole.api.RuntimeVariableContext;
import com.extole.api.campaign.VariableContext;
import com.extole.api.step.StepContext;

public interface StepDataContext extends StepContext, VariableContext, RuntimeVariableContext {

    String getStepEventId();

    @Nullable
    String typedSource(String sourceType, Object source);

    @Nullable
    Object getLatestJourneyDataValue(String dataName);

    @Nullable
    String getReferralSource();

    @Nullable
    String getQuality();

    StepTriggerResult[] getTriggerResults();

}
