package com.extole.api.service.person;

import javax.annotation.Nullable;

import com.extole.api.person.Person;

public interface PersonLookupBuilder {
    PersonLookupBuilder withPersonId(String personId);

    PersonLookupBuilder withEmail(String email);

    PersonLookupBuilder withPersonKey(String type, String value);

    @Nullable
    Person lookup();
}
