package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface PersonService {
    PersonBuilder createPerson();

    PersonLookupBuilder lookupPerson();
}
