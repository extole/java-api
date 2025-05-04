package com.extole.api.prehandler_legacy;

import javax.annotation.Nullable;

import com.extole.api.event.InputConsumerEvent;
import com.extole.api.person.Authorization;
import com.extole.api.person.Person;

@Deprecated // TODO to be removed in ENG-13399
public interface LegacyPrehandlerConditionContext {
    Authorization getAuthorization();

    InputConsumerEvent getEvent();

    @Deprecated // TODO remove it because it is available in InputEvent, ENG-10118
    String getEventName();

    @Nullable
    Person findPersonById(String personId);

    @Nullable
    Person findPersonByLookupKey(String lookupType, String key);

    @Nullable
    Object jsonPath(Object object, String path);

}
