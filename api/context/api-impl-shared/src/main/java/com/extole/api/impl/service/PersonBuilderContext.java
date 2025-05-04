package com.extole.api.impl.service;

import java.util.Optional;

import com.extole.authorization.service.ClientHandle;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.id.Id;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonService;
import com.extole.sandbox.SandboxService;
import com.extole.security.backend.BackendAuthorizationProvider;

public final class PersonBuilderContext {

    private final SandboxService sandboxService;
    private final Id<ClientHandle> clientId;
    private final VerifiedEmailService verifiedEmailService;
    private final PersonService personService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final ProgramDomainCache programDomainCache;
    private final PersonOperations personOperations;

    private final Optional<Id<ProgramHandle>> clientDomainId;

    private PersonBuilderContext(
        SandboxService sandboxService,
        Id<ClientHandle> clientId,
        VerifiedEmailService verifiedEmailService,
        PersonService personService,
        BackendAuthorizationProvider backendAuthorizationProvider,
        ProgramDomainCache programDomainCache,
        PersonOperations personOperations,
        Optional<Id<ProgramHandle>> clientDomainId) {
        this.sandboxService = sandboxService;
        this.clientId = clientId;
        this.verifiedEmailService = verifiedEmailService;
        this.personService = personService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.programDomainCache = programDomainCache;
        this.personOperations = personOperations;
        this.clientDomainId = clientDomainId;
    }

    public Id<ClientHandle> getClientId() {
        return clientId;
    }

    public VerifiedEmailService getVerifiedEmailService() {
        return verifiedEmailService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public BackendAuthorizationProvider getBackendAuthorizationProvider() {
        return backendAuthorizationProvider;
    }

    public ProgramDomainCache getProgramDomainCache() {
        return programDomainCache;
    }

    public PersonOperations getPersonOperations() {
        return personOperations;
    }

    public Optional<Id<ProgramHandle>> getClientDomainId() {
        return clientDomainId;
    }

    public SandboxService getSandboxService() {
        return sandboxService;
    }

    public static final class Builder {

        private final SandboxService sandboxService;
        private final Id<ClientHandle> clientId;
        private final VerifiedEmailService verifiedEmailService;
        private final PersonService personService;
        private final BackendAuthorizationProvider backendAuthorizationProvider;
        private final ProgramDomainCache programDomainCache;
        private final PersonOperations personOperations;

        private Optional<Id<ProgramHandle>> clientDomainId = Optional.empty();

        public Builder(
            SandboxService sandboxService,
            Id<ClientHandle> clientId,
            VerifiedEmailService verifiedEmailService,
            PersonService personService,
            BackendAuthorizationProvider backendAuthorizationProvider,
            ProgramDomainCache programDomainCache,
            PersonOperations personOperations) {
            this.sandboxService = sandboxService;
            this.clientId = clientId;
            this.verifiedEmailService = verifiedEmailService;
            this.personService = personService;
            this.backendAuthorizationProvider = backendAuthorizationProvider;
            this.programDomainCache = programDomainCache;
            this.personOperations = personOperations;
        }

        public Builder withClientDomainId(Id<ProgramHandle> clientDomainId) {
            this.clientDomainId = Optional.of(clientDomainId);
            return this;
        }

        public PersonBuilderContext build() {
            return new PersonBuilderContext(sandboxService, clientId, verifiedEmailService, personService,
                backendAuthorizationProvider, programDomainCache, personOperations, clientDomainId);
        }

    }

}
