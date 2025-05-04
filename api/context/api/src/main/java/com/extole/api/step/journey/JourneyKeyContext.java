package com.extole.api.step.journey;

import javax.annotation.Nullable;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.Sandbox;
import com.extole.api.person.Person;

public interface JourneyKeyContext extends GlobalContext, LoggerContext {

    Person getPerson();

    ConsumerEvent getCauseEvent();

    String getCampaignId();

    String getProgramLabel();

    String getStepName();

    String getJourneyName();

    Sandbox getSandbox();

    @Nullable
    Person getOtherPerson();

    String getStepEventId();

}
