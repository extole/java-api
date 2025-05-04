package com.extole.api.impl.service;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.impl.NullArgumentRuntimeException;
import com.extole.api.impl.person.PersonImpl;
import com.extole.api.person.Person;
import com.extole.api.service.PersonLookupBuilder;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.sandbox.SandboxService;
import com.extole.security.backend.BackendAuthorizationProvider;

public class PersonLookupBuilderImpl implements PersonLookupBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PersonLookupBuilderImpl.class);

    private final SandboxService sandboxService;
    private final PersonService personService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final Id<ClientHandle> clientId;

    private String personId;
    private PersonKey personKey;

    public PersonLookupBuilderImpl(
        SandboxService sandboxService,
        Id<ClientHandle> clientId,
        PersonService personService,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.sandboxService = sandboxService;
        this.clientId = clientId;
        this.personService = personService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    @Override
    public PersonLookupBuilder withPersonId(String personId) {
        this.personId = personId;
        return this;
    }

    @Override
    public PersonLookupBuilder withEmail(String email) {
        if (email == null) {
            throw new NullArgumentRuntimeException("Cannot lookup by null email");
        }
        this.personKey = PersonKey.ofEmailType(email);
        return this;
    }

    @Override
    public PersonLookupBuilder withPersonKey(String type, String value) {
        if (type == null || value == null) {
            throw new NullArgumentRuntimeException("Cannot lookup by null type or value");
        }
        this.personKey = PersonKey.ofType(type, value);
        return this;
    }

    @Nullable
    @Override
    public Person lookup() {
        com.extole.person.service.profile.Person person = null;
        try {
            Authorization authorization = backendAuthorizationProvider.getAuthorizationForBackend(clientId);
            if (!Strings.isNullOrEmpty(personId)) {
                person = personService.getPerson(authorization, Id.valueOf(personId));
            } else if (personKey != null) {
                person = personService.getPersonByProfileLookupKey(authorization, personKey).orElse(null);
            }
        } catch (PersonNotFoundException ignored) {
            // ignored
        } catch (AuthorizationException e) {
            LOG.error("Unable to lookup person for clientId={} with [personId={}, personKey={}]", clientId, personId,
                personKey, e);
        }
        return person != null ? new PersonImpl(person, sandboxService) : null;
    }

}
