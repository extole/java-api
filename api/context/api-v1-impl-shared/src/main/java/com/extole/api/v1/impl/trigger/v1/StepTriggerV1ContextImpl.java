package com.extole.api.v1.impl.trigger.v1;

import javax.annotation.Nullable;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.v1.trigger.v1.StepTriggerV1Context;
import com.extole.event.consumer.ConsumerEvent;

public class StepTriggerV1ContextImpl implements StepTriggerV1Context {

    private static final Logger LOG = LoggerFactory.getLogger(StepTriggerV1ContextImpl.class);

    private final ConsumerEvent event;

    public StepTriggerV1ContextImpl(ConsumerEvent event) {
        this.event = event;
    }

    @Override
    public ConsumerEvent getEvent() {
        return event;
    }

    @Override
    @Nullable
    public Object jsonPath(Object object, String path) {
        try {
            return JsonPath.read(object, path);
        } catch (PathNotFoundException e) {
            return null;
        } catch (RuntimeException e) {
            LOG.error("Unable to evaluate JSON path: {} on the object of type: {} (eventId: {})", path,
                object.getClass(), event.getId(), e);
            return null;
        }
    }

}
