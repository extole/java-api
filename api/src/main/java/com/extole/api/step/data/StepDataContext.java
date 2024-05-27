package com.extole.api.step.data;

import javax.annotation.Nullable;

import com.extole.api.event.Sandbox;
import com.extole.api.person.Person;
import com.extole.api.person.PersonJourney;
import com.extole.api.step.StepContext;

public interface StepDataContext extends StepContext {

    @Nullable
    String typedSource(String sourceType, String source);

    @Nullable
    PersonJourney getJourney();

    @Nullable
    Object getLatestJourneyDataValue(String dataName);

    @Nullable
    String getReferralSource();

    @Nullable
    Person getOtherPerson();

    Sandbox getSandbox();

}
