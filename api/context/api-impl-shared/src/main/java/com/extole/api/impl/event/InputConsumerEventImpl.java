package com.extole.api.impl.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.ClientContext;
import com.extole.api.ClientDomainContext;
import com.extole.api.event.EventContext;
import com.extole.api.event.InputConsumerEvent;
import com.extole.api.event.InputEventLabel;
import com.extole.api.event.InputEventLocale;
import com.extole.api.event.Sandbox;
import com.extole.api.person.Person;
import com.extole.common.ip.deprecated.Ip;
import com.extole.common.lang.ToString;

public final class InputConsumerEventImpl extends ConsumerEventImpl implements InputConsumerEvent {

    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerEventImpl.class);

    private final String url;
    private final Optional<String> referrer;
    private final String name;
    private final List<String> sourceIps;
    private final Map<String, List<String>> httpHeaders;
    private final Map<String, List<String>> httpCookies;
    private final List<String> handlerMessages;
    private final List<InputEventLabel> labels;
    private final InputEventLocale locale;

    private InputConsumerEventImpl(String eventTime,
        Sandbox sandbox,
        String requestTime,
        ClientContext clientContext,
        ClientDomainContext clientDomainContext,
        String eventType,
        String eventId,
        String causeEventId,
        String rootEventId,
        EventContext eventContext,
        Integer eventSequence,
        Map<String, Object> data,
        Person person,
        List<String> handlerMessages,
        List<String> sourceIps,
        Map<String, List<String>> httpCookies,
        Map<String, List<String>> httpHeaders,
        Optional<String> referrer,
        String url,
        InputEventLocale locale,
        List<InputEventLabel> labels,
        String name) {
        super(eventTime, sandbox, requestTime, clientContext, clientDomainContext, eventType, eventId, causeEventId,
            rootEventId, eventContext, eventSequence.intValue(), data, person);
        this.url = url;
        this.referrer = referrer;
        this.name = name;
        this.sourceIps = sourceIps;
        this.httpHeaders = httpHeaders;
        this.httpCookies = httpCookies;
        this.handlerMessages = handlerMessages;
        this.labels = labels;
        this.locale = locale;
    }

    private InputConsumerEventImpl(com.extole.event.consumer.input.InputConsumerEvent event, Person person) {
        super(event, person);
        this.url = event.getRawEvent().getUrl();
        this.referrer = Optional.ofNullable(event.getRawEvent().getReferrer());
        this.name = event.getName();
        this.sourceIps = Collections.unmodifiableList(
            event.getRawEvent().getSourceIps().stream()
                .map(Ip::getAddress)
                .collect(Collectors.toList()));
        this.httpHeaders = Collections.unmodifiableMap(event.getRawEvent().getHttpHeaders());
        this.httpCookies = Collections.unmodifiableMap(event.getRawEvent().getHttpCookies());
        this.handlerMessages = ImmutableList.of();
        this.labels = Collections.unmodifiableList(
            event.getLabels().stream()
                .map(label -> new InputEventLabelImpl(label.getName(), label.isRequired()))
                .collect(Collectors.toList()));
        this.locale = new InputEventLocaleImpl(
            event.getLocale().getUserSpecified(),
            event.getLocale().getLastBrowser());
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getReferrer() {
        return referrer.orElse(null);
    }

    @Override
    public List<String> getSourceIps() {
        return sourceIps;
    }

    @Override
    public Map<String, List<String>> getHttpHeaders() {
        return httpHeaders;
    }

    @Override
    public Map<String, List<String>> getHttpCookies() {
        return httpCookies;
    }

    @Override
    public List<String> getHandlerMessages() {
        return handlerMessages;
    }

    @Override
    public String getEventName() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputEventLabel[] getLabels() {
        return labels.toArray(new InputEventLabel[] {});
    }

    @Override
    public InputEventLocale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

    public static InputConsumerEvent newInstance(com.extole.event.consumer.input.InputConsumerEvent event,
        Person person) {
        return new InputConsumerEventImpl(event, person);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ConsumerEventImpl.Builder {

        private List<String> handlerMessages;
        private List<String> sourceIps;
        private Map<String, List<String>> httpCookies;
        private Map<String, List<String>> httpHeaders;
        private Optional<String> referrer = Optional.empty();
        private String url;
        private InputEventLocale locale;
        private List<InputEventLabel> labels;
        private String name;

        private Builder() {
        }

        public Builder withHandlerMessages(List<String> handlerMessages) {
            this.handlerMessages = handlerMessages;
            return this;
        }

        public Builder withSourceIps(List<String> sourceIps) {
            this.sourceIps = sourceIps;
            return this;
        }

        public Builder withHttpCookies(Map<String, List<String>> httpCookies) {
            this.httpCookies = httpCookies;
            return this;
        }

        public Builder withHttpHeaders(Map<String, List<String>> httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder withReferrer(String referrer) {
            this.referrer = Optional.ofNullable(referrer);
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withLabels(List<InputEventLabel> labels) {
            this.labels = labels;
            return this;
        }

        public Builder withLocale(InputEventLocale locale) {
            this.locale = locale;
            return this;
        }

        @Override
        public InputConsumerEventImpl build() {
            Preconditions.checkNotNull(person, "Person should be set");
            Preconditions.checkNotNull(data, "Data should be set");
            Preconditions.checkNotNull(eventTime, "EventTime should be set");
            Preconditions.checkNotNull(requestTime, "RequestTime should be set");
            Preconditions.checkNotNull(clientContext, "ClientContext should be set");
            Preconditions.checkNotNull(clientDomainContext, "ClientDomainContext should be set");
            Preconditions.checkNotNull(eventType, "EventType should be set");
            Preconditions.checkNotNull(eventId, "EventId should be set");
            Preconditions.checkNotNull(eventContext, "EventContext should be set");
            Preconditions.checkNotNull(Integer.valueOf(eventSequence), "EventSequence should be set");
            Preconditions.checkNotNull(sandbox, "Sandbox should be set");

            if (url == null) {
                LOG.warn("For InputConsumerEvent Url should be set, event id: " + eventId);
            }
            if (sourceIps == null) {
                LOG.warn("For InputConsumerEvent sourceIps should be set, event id: " + eventId);
            }
            if (httpHeaders == null) {
                LOG.warn("For InputConsumerEvent httpHeaders should be set, event id: " + eventId);
            }
            if (httpCookies == null) {
                LOG.warn("For InputConsumerEvent httpCookies should be set, event id: " + eventId);
            }
            if (handlerMessages == null) {
                LOG.warn("For InputConsumerEvent handlerMessages should be set, event id: " + eventId);
            }
            if (labels == null) {
                LOG.warn("For InputConsumerEvent labels should be set, event id: " + eventId);
            }
            if (locale == null) {
                LOG.warn("For InputConsumerEvent locale should be set, event id: " + eventId);
            }
            if (name == null) {
                LOG.warn("For InputConsumerEvent name should be set, event id: " + eventId);
            }
            return new InputConsumerEventImpl(eventTime, sandbox, requestTime, clientContext, clientDomainContext,
                eventType, eventId, causeEventId, rootEventId, eventContext, Integer.valueOf(eventSequence), data,
                person, handlerMessages, sourceIps, httpCookies, httpHeaders, referrer, url, locale, labels, name);
        }
    }

}
