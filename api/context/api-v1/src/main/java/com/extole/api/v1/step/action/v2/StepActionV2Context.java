package com.extole.api.v1.step.action.v2;

import javax.annotation.Nullable;

import com.extole.api.person.Person;
import com.extole.api.service.GlobalServices;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.event.consumer.step.StepConsumerEvent;

@Deprecated // TODO requires update - properly defined API/context, no dependency on consumer event model - ENG-8636
public interface StepActionV2Context {

    ConsumerEvent getEvent();

    StepConsumerEvent getStepEvent();

    Person getPerson();

    @Nullable
    Object jsonPath(Object object, String path);

    GlobalServices getGlobalServices();

}
