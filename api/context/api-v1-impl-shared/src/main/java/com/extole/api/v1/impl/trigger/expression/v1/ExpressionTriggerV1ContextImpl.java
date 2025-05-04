package com.extole.api.v1.impl.trigger.expression.v1;

import javax.annotation.Nullable;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.v1.trigger.expression.v1.ExpressionTriggerV1Context;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.person.service.profile.Person;

public class ExpressionTriggerV1ContextImpl implements ExpressionTriggerV1Context {

    private static final Logger LOG = LoggerFactory.getLogger(ExpressionTriggerV1ContextImpl.class);

    private final ConsumerEvent event;
    private final Person person;

    public ExpressionTriggerV1ContextImpl(ConsumerEvent event, Person person) {
        this.event = event;
        this.person = person;
    }

    @Override
    public ConsumerEvent getEvent() {
        return event;
    }

    @Override
    public Person getPerson() {
        return person;
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
