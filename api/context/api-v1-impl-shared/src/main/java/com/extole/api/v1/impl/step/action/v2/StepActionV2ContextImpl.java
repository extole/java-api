package com.extole.api.v1.impl.step.action.v2;

import javax.annotation.Nullable;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.impl.person.PersonImpl;
import com.extole.api.person.Person;
import com.extole.api.service.GlobalServices;
import com.extole.api.v1.step.action.v2.StepActionV2Context;
import com.extole.event.consumer.ConsumerEvent;
import com.extole.event.consumer.step.StepConsumerEvent;
import com.extole.sandbox.SandboxService;

public class StepActionV2ContextImpl implements StepActionV2Context {

    private static final Logger LOG = LoggerFactory.getLogger(StepActionV2ContextImpl.class);

    private final ConsumerEvent event;
    private final StepConsumerEvent stepEvent;
    private final Person person;
    private final GlobalServices globalServices;

    public StepActionV2ContextImpl(
        SandboxService sandboxService,
        ConsumerEvent event,
        StepConsumerEvent stepEvent,
        com.extole.person.service.profile.Person person,
        GlobalServices globalServices) {
        this.event = event;
        this.stepEvent = stepEvent;
        this.person = new PersonImpl(person, sandboxService);
        this.globalServices = globalServices;
    }

    @Override
    public ConsumerEvent getEvent() {
        return event;
    }

    @Override
    public StepConsumerEvent getStepEvent() {
        return stepEvent;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public GlobalServices getGlobalServices() {
        return globalServices;
    }

    @Override
    @Nullable
    public Object jsonPath(Object object, String path) {
        try {
            return JsonPath.read(object, path);
        } catch (PathNotFoundException e) {
            return null;
        } catch (RuntimeException e) {
            LOG.error("Unable to evaluate JSON path: {} on the object of type: {} (eventId: {}, stepEventId: {})",
                path, object.getClass(), event.getId(), stepEvent.getId(), e);
            return null;
        }
    }

}
