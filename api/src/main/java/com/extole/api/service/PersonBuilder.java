package com.extole.api.service;

import com.extole.api.person.Person;

public interface PersonBuilder {

    enum PersonDataScope {
        PUBLIC, PRIVATE, CLIENT
    }

    PersonBuilder withFirstName(String firstName);

    PersonBuilder clearFirstName();

    PersonBuilder withLastName(String lastName);

    PersonBuilder clearLastName();

    PersonBuilder withProfilePictureUrl(String profilePictureUrl) throws InvalidUriException;

    PersonBuilder clearProfilePictureUrl();

    PersonBuilder withEmail(String email) throws InvalidEmailException;

    PersonBuilder withPartnerUserId(String partnerUserId);

    PersonBuilder withDisplacedPerson(String displacedPersonId);

    PersonBuilder addKey(String type, String value);

    PersonBuilder addData(String name, String value, String scope);

    ShareableCreateBuilder createShareable() throws PersonBuilderException;

    ShareableUpdateBuilder updateShareable(String code);

    Person save() throws PersonBuilderException;

}
