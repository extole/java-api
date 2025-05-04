package com.extole.client.rest.impl.person.provider.legacy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.extole.person.service.profile.Person;

public class LegacyClientEventPersonLookupResultImpl implements LegacyClientEventPersonLookupResult {

    private final Optional<Person> person;
    private final List<String> logMessages;

    public LegacyClientEventPersonLookupResultImpl(List<String> logMessages) {
        this.person = Optional.empty();
        this.logMessages = Collections.unmodifiableList(logMessages);
    }

    public LegacyClientEventPersonLookupResultImpl(Person person, List<String> logMessages) {
        this.person = Optional.of(person);
        this.logMessages = Collections.unmodifiableList(logMessages);
    }

    @Override
    public Optional<Person> getPerson() {
        return person;
    }

    @Override
    public List<String> getLogMessages() {
        return logMessages;
    }

}
