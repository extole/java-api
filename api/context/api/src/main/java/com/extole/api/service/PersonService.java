package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface PersonService {
    PersonBuilder createPerson();

    PersonBuilder updatePerson(String personId);

    PersonLookupBuilder lookupPerson();

    boolean isSamePerson(String firstPersonId, String secondPersonId);

}
