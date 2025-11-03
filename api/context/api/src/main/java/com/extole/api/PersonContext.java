package com.extole.api;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.Person;
import com.extole.api.service.person.PersonBuilder;

@Schema
public interface PersonContext<T extends Person> {

    T getPerson();

    PersonBuilder updatePerson();

}
