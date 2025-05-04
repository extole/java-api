package com.extole.api.v1.trigger.expression.v1;

import com.extole.event.consumer.ConsumerEvent;
import com.extole.person.service.profile.Person;

// TODO ENG-11835 use a common evaluation root object
@Deprecated // TODO requires update - properly defined API/context, no dependency on consumer event model - ENG-8636
public interface ExpressionTriggerV1Context {

    ConsumerEvent getEvent();

    Person getPerson();

    Object jsonPath(Object object, String path);

}
