package com.extole.api.impl.service;

import java.util.Optional;
import java.util.function.Supplier;

import com.extole.api.service.PersonBuilder;
import com.extole.api.service.PersonLookupBuilder;
import com.extole.api.service.PersonService;
import com.extole.authorization.service.ClientHandle;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.id.Id;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.PersonOperations;
import com.extole.sandbox.SandboxService;
import com.extole.security.backend.BackendAuthorizationProvider;

public class PersonServiceImpl implements PersonService {

    private final SandboxService sandboxService;
    private final Id<ClientHandle> clientId;
    private final com.extole.person.service.profile.PersonService personService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final VerifiedEmailService verifiedEmailService;
    private final ProgramDomainCache programDomainCache;
    private final Supplier<PersonOperations> personOperationsSupplier;
    private final Optional<Id<ProgramHandle>> clientDomainId;

    public PersonServiceImpl(
        SandboxService sandboxService,
        Id<ClientHandle> clientId,
        com.extole.person.service.profile.PersonService personService,
        BackendAuthorizationProvider backendAuthorizationProvider,
        VerifiedEmailService verifiedEmailService,
        ProgramDomainCache programDomainCache,
        Supplier<PersonOperations> personOperationsSupplier,
        Optional<Id<ProgramHandle>> clientDomainId) {
        this.sandboxService = sandboxService;
        this.clientId = clientId;
        this.personService = personService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.verifiedEmailService = verifiedEmailService;
        this.programDomainCache = programDomainCache;
        this.personOperationsSupplier = personOperationsSupplier;
        this.clientDomainId = clientDomainId;
    }

    @Override
    public PersonBuilder createPerson() {
        PersonBuilderContext.Builder builder =
            new PersonBuilderContext.Builder(sandboxService, clientId, verifiedEmailService, personService,
                backendAuthorizationProvider, programDomainCache, personOperationsSupplier.get());

        clientDomainId.ifPresent(value -> builder.withClientDomainId(value));

        return PersonBuilderImpl.createPerson(builder.build());
    }

    @Override
    public PersonBuilder updatePerson(String personId) {
        PersonBuilderContext.Builder builder =
            new PersonBuilderContext.Builder(sandboxService, clientId, verifiedEmailService, personService,
                backendAuthorizationProvider, programDomainCache, personOperationsSupplier.get());

        clientDomainId.ifPresent(value -> builder.withClientDomainId(value));
        return PersonBuilderImpl.updatePerson(builder.build(), Id.valueOf(personId));
    }

    @Override
    public PersonLookupBuilder lookupPerson() {
        return new PersonLookupBuilderImpl(sandboxService, clientId, personService, backendAuthorizationProvider);
    }

    @Override
    public boolean isSamePerson(String firstPersonId, String secondPersonId) {
        return personService.isSamePerson(clientId, Id.valueOf(firstPersonId), Id.valueOf(secondPersonId));
    }

}
