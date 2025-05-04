package com.extole.client.rest.impl.person.provider.legacy;

import java.util.Collections;
import java.util.List;

import com.extole.person.service.profile.Person;

public class LegacyClientEventPersonLookupOrCreateResultImpl implements LegacyClientEventPersonLookupOrCreateResult {

    private final Person person;
    private final List<String> logMessages;

    public LegacyClientEventPersonLookupOrCreateResultImpl(Person person, List<String> logMessages) {
        this.person = person;
        this.logMessages = Collections.unmodifiableList(logMessages);
    }

    @Override
    public Person getPerson() {
        return person;
    }

    @Override
    public List<String> getLogMessages() {
        return logMessages;
    }

}
