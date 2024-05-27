package com.extole.api.step;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.event.ConsumerEvent;
import com.extole.api.person.Person;

public interface StepContext extends GlobalContext, LoggerContext {

    Person getPerson();

    ConsumerEvent getCauseEvent();

}
