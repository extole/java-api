package com.extole.consumer.rest.impl.request.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.service.ConsumerRequestContext;

class ConsumerRequestContextImpl implements ConsumerRequestContext {
    private final PersonAuthorization authorization;
    private final ProcessedRawEvent processedRawEvent;
    private final List<String> performanceLogMessages;

    ConsumerRequestContextImpl(PersonAuthorization authorization, ProcessedRawEvent processedRawEvent,
        List<String> performanceLogMessages) {
        this.authorization = authorization;
        this.processedRawEvent = processedRawEvent;
        this.performanceLogMessages =
            performanceLogMessages != null ? Collections.unmodifiableList(new ArrayList<>(performanceLogMessages))
                : Collections.emptyList();
    }

    @Override
    public PersonAuthorization getAuthorization() {
        return authorization;
    }

    @Override
    public ProcessedRawEvent getProcessedRawEvent() {
        return processedRawEvent;
    }

    @Override
    public List<String> getPerformanceLogMessages() {
        return this.performanceLogMessages;
    }

    @Override
    public String toString() {
        return String.format(
            "ConsumerRequestContext[clientId: %s," +
                " personId: %s," +
                " processedRawEvent: %s]",
            authorization.getClientId(), authorization.getIdentityId(), processedRawEvent);
    }

}
