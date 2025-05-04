package com.extole.client.rest.impl.person.provider.legacy;

import java.util.List;

import com.extole.person.service.profile.Person;

public interface LegacyClientEventPersonLookupOrCreateResult {

    Person getPerson();

    List<String> getLogMessages();

}
