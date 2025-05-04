package com.extole.api.impl.event;

import java.util.Collections;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import com.extole.api.ClientContext;
import com.extole.api.ClientDomainContext;
import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.EventContext;
import com.extole.api.event.Sandbox;
import com.extole.api.impl.ClientContextImpl;
import com.extole.api.impl.ClientDomainContextImpl;
import com.extole.api.person.Person;
import com.extole.common.lang.KeyCaseInsensitiveMap;
import com.extole.common.lang.ToString;

public class ConsumerEventImpl implements ConsumerEvent {

    private final Map<String, Object> data;
    private final String eventTime;
    private final Sandbox sandbox;
    private final String consumerEventId;
    private final String causeEventId;
    private final String rootEventId;
    private final String requestTime;
    private final String eventType;
    private final ClientContext clientContext;
    private final ClientDomainContext clientDomainContext;
    private final EventContext eventContext;
    private final int eventSequence;
    private final Person person;

    protected ConsumerEventImpl(com.extole.event.consumer.ConsumerEvent consumerEvent, Person person) {
        this.data = Collections.unmodifiableMap(KeyCaseInsensitiveMap.create(consumerEvent.getData()));
        this.consumerEventId = consumerEvent.getId().getValue();
        this.causeEventId = consumerEvent.getCauseEventId().getValue();
        this.rootEventId = consumerEvent.getRootEventId().getValue();
        this.requestTime = consumerEvent.getRequestTime().toString();
        this.eventType = consumerEvent.getType().name();
        this.eventTime = consumerEvent.getEventTime().toString();
        this.sandbox = new SandboxImpl(consumerEvent.getSandbox());
        this.clientContext = createClientContext(consumerEvent);
        this.clientDomainContext = createClientDomainContext(consumerEvent);
        this.eventContext = createEventContext(consumerEvent);
        this.eventSequence = consumerEvent.getEventSequence().intValue();
        this.person = person;
    }

    protected ConsumerEventImpl(String eventTime,
        Sandbox sandbox,
        String requestTime,
        ClientContext clientContext,
        ClientDomainContext clientDomainContext,
        String eventType,
        String eventId,
        String causeEventId,
        String rootEventId,
        EventContext eventContext,
        int eventSequence,
        Map<String, Object> data,
        Person person) {
        this.data = Collections.unmodifiableMap(KeyCaseInsensitiveMap.create(data));
        this.eventTime = eventTime;
        this.sandbox = sandbox;
        this.requestTime = requestTime;
        this.clientContext = clientContext;
        this.clientDomainContext = clientDomainContext;
        this.eventType = eventType;
        this.consumerEventId = eventId;
        this.causeEventId = causeEventId;
        this.rootEventId = rootEventId;
        this.eventContext = eventContext;
        this.eventSequence = eventSequence;
        this.person = person;
    }

    public static ConsumerEvent newInstance(com.extole.event.consumer.ConsumerEvent consumerEvent, Person person) {
        return new ConsumerEventImpl(consumerEvent, person);
    }

    @Override
    public String getId() {
        return consumerEventId;
    }

    @Override
    public String getCauseEventId() {
        return causeEventId;
    }

    @Override
    public String getRootEventId() {
        return rootEventId;
    }

    @Override
    public Sandbox getSandbox() {
        return sandbox;
    }

    @Override
    public String getType() {
        return eventType;
    }

    @Override
    public String getRequestTime() {
        return requestTime;
    }

    @Override
    public String getEventTime() {
        return eventTime;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public ClientContext getClientContext() {
        return clientContext;
    }

    @Override
    public ClientDomainContext getClientDomainContext() {
        return clientDomainContext;
    }

    @Override
    public EventContext getEventContext() {
        return eventContext;
    }

    @Override
    public int getEventSequence() {
        return eventSequence;
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static ClientDomainContextImpl
        createClientDomainContext(com.extole.event.consumer.ConsumerEvent consumerEvent) {
        return new ClientDomainContextImpl(consumerEvent.getClientDomainContext().getClientDomain(),
            consumerEvent.getClientDomainContext().getClientDomainId().getValue());
    }

    private static ClientContextImpl createClientContext(com.extole.event.consumer.ConsumerEvent consumerEvent) {
        return new ClientContextImpl(consumerEvent.getClientContext().getClientId().getValue(),
            consumerEvent.getClientContext().getClientShortName(),
            consumerEvent.getClientContext().getClientTimeZone().getId());
    }

    private static EventContext createEventContext(com.extole.event.consumer.ConsumerEvent consumerEvent) {
        return new EventContextImpl(consumerEvent.getEventContext().getSourceGeoIps(),
            consumerEvent.getEventContext().getAppType(), consumerEvent.getEventContext().getUserId(),
            consumerEvent.getEventContext().getApiType());
    }

    public static class Builder {

        protected String eventTime;
        protected Sandbox sandbox;
        protected String requestTime;
        protected ClientContext clientContext;
        protected ClientDomainContext clientDomainContext;
        protected String eventType;
        protected String eventId;
        protected String causeEventId;
        protected String rootEventId;
        protected EventContext eventContext = EventContextImpl.empty();
        protected int eventSequence;
        protected Map<String, Object> data;
        protected Person person;

        protected Builder() {
        }

        public Builder withEventTime(String eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            this.data = ImmutableMap.copyOf(data);
            return this;
        }

        public Builder withSandbox(Sandbox sandbox) {
            this.sandbox = sandbox;
            return this;
        }

        public Builder withRequestTime(String requestTime) {
            this.requestTime = requestTime;
            return this;
        }

        public Builder withClientContext(ClientContext clientContext) {
            this.clientContext = clientContext;
            return this;
        }

        public Builder withClientDomainContext(ClientDomainContext clientDomainContext) {
            this.clientDomainContext = clientDomainContext;
            return this;
        }

        public Builder withEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder withEventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withCauseEventId(String causeEventId) {
            this.causeEventId = causeEventId;
            return this;
        }

        public Builder withRootEventId(String rootEventId) {
            this.rootEventId = rootEventId;
            return this;
        }

        public Builder withEventContext(EventContext eventContext) {
            this.eventContext = eventContext;
            return this;
        }

        public Builder withEventSequence(int eventSequence) {
            this.eventSequence = eventSequence;
            return this;
        }

        public Builder withPerson(Person person) {
            this.person = person;
            return this;
        }

        public ConsumerEventImpl build() {
            Preconditions.checkNotNull(person, "Person should be set");
            Preconditions.checkNotNull(data, "Data should be set");
            Preconditions.checkNotNull(eventTime, "EventTime should be set");
            Preconditions.checkNotNull(requestTime, "RequestTime should be set");
            Preconditions.checkNotNull(clientContext, "ClientContext should be set");
            Preconditions.checkNotNull(clientDomainContext, "ClientDomainContext should be set");
            Preconditions.checkNotNull(eventType, "EventType should be set");
            Preconditions.checkNotNull(eventId, "EventId should be set");
            Preconditions.checkNotNull(causeEventId, "CauseEventId should be set");
            Preconditions.checkNotNull(rootEventId, "RootEventId should be set");
            Preconditions.checkNotNull(sandbox, "Sandbox should be set");
            Preconditions.checkNotNull(Integer.valueOf(eventSequence), "EventSequence should be set");

            return new ConsumerEventImpl(eventTime, sandbox, requestTime, clientContext, clientDomainContext,
                eventType, eventId, causeEventId, rootEventId, eventContext, eventSequence, data, person);
        }

    }

}
