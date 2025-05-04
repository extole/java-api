package com.extole.api.step.action.expression;

import javax.annotation.Nullable;

import com.extole.api.RuntimeVariableContext;
import com.extole.api.campaign.VariableContext;
import com.extole.api.event.internal.InternalConsumerEventBuilder;
import com.extole.api.person.JourneyKey;
import com.extole.api.person.PersonJourney;
import com.extole.api.service.AudienceMembershipService;
import com.extole.api.service.RewardService;
import com.extole.api.service.StepSignalBuilder;
import com.extole.api.step.action.AsyncActionContext;

public interface ExpressionActionCommandContext extends AsyncActionContext, VariableContext, RuntimeVariableContext {

    InternalConsumerEventBuilder internalConsumerEventBuilder();

    AudienceMembershipService getAudienceMembershipService();

    RewardService getRewardService();

    StepSignalBuilder stepSignalBuilder(String pollingId);

    @Nullable
    PersonJourney getJourney();

    @Nullable
    JourneyKey getJourneyKey();

}
