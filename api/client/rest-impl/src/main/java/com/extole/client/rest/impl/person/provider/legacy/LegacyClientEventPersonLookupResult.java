package com.extole.client.rest.impl.person.provider.legacy;

import java.util.List;
import java.util.Optional;

import com.extole.person.service.profile.Person;

public interface LegacyClientEventPersonLookupResult {

    Optional<Person> getPerson();

    List<String> getLogMessages();

}
