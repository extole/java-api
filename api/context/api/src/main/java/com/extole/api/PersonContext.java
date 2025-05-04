package com.extole.api;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.Person;
import com.extole.api.service.PersonBuilder;

@Schema
public interface PersonContext {

    Person getPerson();

    PersonBuilder updatePerson();

}
