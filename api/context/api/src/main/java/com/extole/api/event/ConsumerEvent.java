package com.extole.api.event;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.ClientContext;
import com.extole.api.ClientDomainContext;
import com.extole.api.person.Person;

@Schema
public interface ConsumerEvent {

    String getId();

    String getCauseEventId();

    String getRootEventId();

    Map<String, Object> getData();

    String getType();

    String getRequestTime();

    String getEventTime();

    Sandbox getSandbox();

    ClientContext getClientContext();

    ClientDomainContext getClientDomainContext();

    EventContext getEventContext();

    int getEventSequence();

    Person getPerson();
}
