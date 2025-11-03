package com.extole.api.step;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.RuntimeVariableContext;
import com.extole.api.campaign.VariableContext;
import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.Sandbox;
import com.extole.api.person.JourneyKey;
import com.extole.api.person.Person;
import com.extole.api.person.PersonJourney;
import com.extole.api.step.campaign.RunningCampaign;

public interface StepContext extends GlobalContext, LoggerContext, VariableContext, RuntimeVariableContext {

    RunningCampaign getCampaign();

    Person getPerson();

    ConsumerEvent getCauseEvent();

    String getCampaignId();

    String getProgramLabel();

    String getStepName();

    String getJourneyName();

    Sandbox getSandbox();

    // TODO Remove and use getCandidateJourney() instead. Review StepContext class
    // hierarchy - ENG-24534
    @Nullable
    PersonJourney getJourney();

    @Nullable
    PersonJourney getCandidateJourney();

    @Nullable
    Person getOtherPerson();

    @Nullable
    JourneyKey getJourneyKey();

}
