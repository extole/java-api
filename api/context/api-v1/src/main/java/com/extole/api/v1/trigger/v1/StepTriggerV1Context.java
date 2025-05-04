package com.extole.api.v1.trigger.v1;

import javax.annotation.Nullable;

import com.extole.event.consumer.ConsumerEvent;

// TODO ENG-11835 use a common evaluation root object
@Deprecated // TODO requires update - properly defined API/context, no dependency on consumer event model - ENG-8636
public interface StepTriggerV1Context {

    ConsumerEvent getEvent();

    @Nullable
    Object jsonPath(Object object, String path);

}
