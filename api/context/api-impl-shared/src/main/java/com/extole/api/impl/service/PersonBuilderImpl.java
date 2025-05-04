package com.extole.api.impl.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Strings;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.impl.person.PersonImpl;
import com.extole.api.person.Person;
import com.extole.api.service.InvalidEmailException;
import com.extole.api.service.InvalidUriException;
import com.extole.api.service.PersonBuilder;
import com.extole.api.service.PersonBuilderException;
import com.extole.api.service.ShareableCreateBuilder;
import com.extole.api.service.ShareableUpdateBuilder;
import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.common.email.Email;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.id.Id;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.InvalidProfileBlockReasonException;
import com.extole.person.service.profile.PersonBlockType;
import com.extole.person.service.profile.PersonData;
import com.extole.person.service.profile.PersonDataInvalidNameException;
import com.extole.person.service.profile.PersonDataInvalidValueException;
import com.extole.person.service.profile.PersonDataNameLengthException;
import com.extole.person.service.profile.PersonDataValueLengthException;
import com.extole.person.service.profile.PersonDisplacedAlreadyDefinedException;
import com.extole.person.service.profile.PersonDisplacedIdentityException;
import com.extole.person.service.profile.PersonDisplacedMissingIdentityException;
import com.extole.person.service.profile.PersonEmailAlreadyDefinedException;
import com.extole.person.service.profile.PersonFirstNameInvalidLengthException;
import com.extole.person.service.profile.PersonHandle;
import com.extole.person.service.profile.PersonLastNameInvalidLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonPartnerUserIdAlreadyDefinedException;
import com.extole.person.service.profile.PersonPartnerUserIdInvalidLengthException;
import com.extole.person.service.profile.PersonSelfDisplacingException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ReadOnlyPersonDataException;
import com.extole.person.service.profile.referral.PersonReferral;
import com.extole.person.service.shareable.ShareableBlockedUrlException;
import com.extole.person.service.shareable.ShareableCodeReservedException;
import com.extole.person.service.shareable.ShareableCodeTakenByPromotionException;
import com.extole.person.service.shareable.ShareableCodeTakenException;
import com.extole.person.service.shareable.ShareableContentDescriptionTooLongException;
import com.extole.person.service.shareable.ShareableDataAttributeNameInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeNameLengthException;
import com.extole.person.service.shareable.ShareableDataAttributeValueInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeValueLengthException;
import com.extole.person.service.shareable.ShareableFieldLengthException;
import com.extole.person.service.shareable.ShareableFieldValueException;
import com.extole.person.service.shareable.ShareableLabelIllegalCharacterInNameException;
import com.extole.person.service.shareable.ShareableLabelNameLengthException;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableProgramNotFoundException;
import com.extole.sandbox.SandboxService;
import com.extole.security.backend.BackendAuthorizationProvider;

public final class PersonBuilderImpl implements PersonBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(PersonBuilderImpl.class);

    private final SandboxService sandboxService;
    private final Id<ClientHandle> clientId;
    private final VerifiedEmailService verifiedEmailService;
    private final PersonService personService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;
    private final ProgramDomainCache programDomainCache;
    private final PersonOperations personOperations;
    private final Optional<Id<ProgramHandle>> clientDomainId;

    private final Optional<Id<PersonHandle>> personId;

    private final List<Pair<String, String>> personKeys = new ArrayList<>();
    private final List<Triple<String, String, PersonData.Scope>> personData = new ArrayList<>();
    private final List<ShareableCreateBuilderImpl> shareableCreateBuilders = new ArrayList<>();
    private final List<ShareableUpdateBuilderImpl> shareableUpdateBuilders = new ArrayList<>();
    private Email email;
    private String firstName;
    private String lastName;
    private URI profilePictureUrl;
    private String partnerUserId;
    private String displacedPersonId;
    private boolean clearFirstName = false;
    private boolean clearLastName = false;
    private boolean clearProfilePictureUrl = false;
    private Optional<PersonBlock> personBlocked = Optional.empty();

    private PersonBuilderImpl(
        SandboxService sandboxService,
        Id<ClientHandle> clientId,
        VerifiedEmailService verifiedEmailService,
        PersonService personService,
        BackendAuthorizationProvider backendAuthorizationProvider,
        ProgramDomainCache programDomainCache,
        PersonOperations personOperations,
        Optional<Id<ProgramHandle>> clientDomainId,
        Optional<Id<PersonHandle>> personId) {
        this.sandboxService = sandboxService;
        this.clientId = clientId;
        this.verifiedEmailService = verifiedEmailService;
        this.personService = personService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
        this.programDomainCache = programDomainCache;
        this.personOperations = personOperations;
        this.clientDomainId = clientDomainId;
        this.personId = personId;
    }

    @Override
    public PersonBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        this.clearFirstName = false;
        return this;
    }

    @Override
    public PersonBuilder clearFirstName() {
        this.clearFirstName = true;
        return this;
    }

    @Override
    public PersonBuilder withLastName(String lastName) {
        this.lastName = lastName;
        this.clearLastName = false;
        return this;
    }

    @Override
    public PersonBuilder clearLastName() {
        this.clearLastName = true;
        return this;
    }

    @Override
    public PersonBuilder withProfilePictureUrl(String profilePictureUrl) throws InvalidUriException {
        try {
            this.profilePictureUrl = new URI(profilePictureUrl);
        } catch (URISyntaxException e) {
            throw new InvalidUriException("Invalid url: " + profilePictureUrl, e);
        }
        this.clearProfilePictureUrl = false;
        return this;
    }

    @Override
    public PersonBuilder clearProfilePictureUrl() {
        this.clearProfilePictureUrl = true;
        return this;
    }

    @Override
    public PersonBuilder withEmail(String email) throws InvalidEmailException {
        try {
            this.email = verifiedEmailService.verifyEmail(email).getEmail();
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            throw new InvalidEmailException("Invalid email: " + email, e);
        }
        return this;
    }

    @Override
    public PersonBuilder withPartnerUserId(String partnerUserId) {
        this.partnerUserId = partnerUserId;
        return this;
    }

    @Override
    public PersonBuilder withDisplacedPerson(String displacedPersonId) {
        this.displacedPersonId = displacedPersonId;
        return this;
    }

    @Override
    public PersonBuilder addKey(String type, String value) {
        personKeys.add(Pair.of(type, value));
        return this;
    }

    @Override
    public PersonBuilder withProfileBlock(String message) {
        this.personBlocked = Optional.of(new PersonBlock(message));
        return this;
    }

    @Override
    public PersonBuilder addData(String name, String value, String scope) {
        personData.add(Triple.of(name, value, PersonData.Scope.valueOf(scope)));
        return this;
    }

    @Override
    public ShareableCreateBuilder createShareable() throws PersonBuilderException {
        Id<ProgramHandle> shareableClientDomainId;
        if (clientDomainId.isPresent()) {
            shareableClientDomainId = clientDomainId.get();
        } else {
            shareableClientDomainId = programDomainCache.getDefaultProgram(clientId)
                .map(clientDomain -> clientDomain.getId())
                .orElseThrow(() -> new PersonBuilderException("Client has not active domain=" + clientId));
        }

        ShareableCreateBuilderImpl shareableCreateBuilder =
            new ShareableCreateBuilderImpl(this, shareableClientDomainId);
        shareableCreateBuilders.add(shareableCreateBuilder);

        return shareableCreateBuilder;
    }

    @Override
    public ShareableUpdateBuilder updateShareable(String code) {
        ShareableUpdateBuilderImpl shareableUpdateBuilder = new ShareableUpdateBuilderImpl(this, code);
        shareableUpdateBuilders.add(shareableUpdateBuilder);

        return shareableUpdateBuilder;
    }

    @Override
    public Person save() throws PersonBuilderException {
        com.extole.person.service.profile.Person person;
        if (personId.isPresent()) {
            try {
                // TODO figure out the correct way to get/create authorization on person updates ENG-14737
                Authorization authorization = backendAuthorizationProvider.getAuthorizationForBackend(clientId);
                person = personService.updatePerson(authorization, personId.get(),
                    new LockDescription("context-api-person-update"),
                    (personBuilder, originalPersonProfile) -> {
                        if (this.personBlocked.isPresent() && !originalPersonProfile.isBlocked()) {
                            blockProfile(personBuilder, originalPersonProfile, this.personBlocked.get().getMessage(),
                                authorization);
                        }
                        return updatePerson(personBuilder);
                    }, personOperations);
            } catch (PersonNotFoundException | AuthorizationException | LockClosureException e) {
                LOG.debug("Unable to update person: {} for client: {}", personId, clientId, e);
                throw new PersonBuilderException("Unable to update person: " + personId
                    + " for client: " + clientId, e);
            }
        } else {
            try {
                person = personService.newPerson(clientId, new LockDescription("context-api-person-create"),
                    personBuilder -> updatePerson(personBuilder), personOperations);
            } catch (LockClosureException e) {
                LOG.debug("Unable to create new person for client: {}", clientId, e);
                throw new PersonBuilderException("Unable to create new person for client: " + clientId, e);
            }
        }

        return new PersonImpl(person, sandboxService);
    }

    private void blockProfile(com.extole.person.service.profile.PersonBuilder personBuilder,
        com.extole.person.service.profile.Person person, String blockMessage, Authorization authorization) {

        String blockReason = String.format("Person with id %s was blocked with this message: %s", person.getId(),
            blockMessage);
        try {
            personBuilder.updateBlock(PersonBlockType.ALL_REWARDING, authorization.getIdentity().getId(), blockReason);
        } catch (InvalidProfileBlockReasonException e) {
            throw new IllegalStateException(e);
        }
    }

    private com.extole.person.service.profile.Person
        updatePerson(com.extole.person.service.profile.PersonBuilder personBuilder) throws LockClosureException {
        try {
            if (email != null) {
                personBuilder.withEmail(email);
            }
            if (clearFirstName) {
                personBuilder.withFirstName(null);
            } else if (!Strings.isNullOrEmpty(firstName)) {
                personBuilder.withFirstName(firstName);
            }
            if (clearLastName) {
                personBuilder.withLastName(null);
            } else if (!Strings.isNullOrEmpty(lastName)) {
                personBuilder.withLastName(lastName);
            }
            if (clearProfilePictureUrl) {
                personBuilder.withProfilePictureUrl(null);
            } else if (profilePictureUrl != null) {
                personBuilder.withProfilePictureUrl(profilePictureUrl);
            }
            if (!Strings.isNullOrEmpty(partnerUserId)) {
                personBuilder.withPartnerUserId(partnerUserId);
            }
            if (!Strings.isNullOrEmpty(displacedPersonId)) {
                personBuilder.withDisplacedPerson(Id.valueOf(displacedPersonId))
                    .withDisplaceRecentReferral(PersonReferral.Side.FRIEND)
                    .withDisplaceRecentReferral(PersonReferral.Side.ADVOCATE)
                    .withDisplaceMostRecentJourney();
            }
            for (Pair<String, String> personKey : personKeys) {
                personBuilder.addKey(personKey.getKey(), personKey.getValue());
            }
            for (Triple<String, String, PersonData.Scope> data : personData) {
                personBuilder.addOrReplaceData(data.getLeft()).withValue(data.getMiddle())
                    .withScope(data.getRight());
            }
            for (ShareableCreateBuilderImpl shareableCreateBuilder : shareableCreateBuilders) {
                shareableCreateBuilder.build(personBuilder);
            }
            for (ShareableUpdateBuilderImpl shareableUpdateBuilder : shareableUpdateBuilders) {
                shareableUpdateBuilder.build(personBuilder);
            }
            return personBuilder.save();
        } catch (PersonFirstNameInvalidLengthException | PersonLastNameInvalidLengthException
            | PersonEmailAlreadyDefinedException | PersonPartnerUserIdAlreadyDefinedException
            | PersonPartnerUserIdInvalidLengthException | PersonDisplacedAlreadyDefinedException
            | PersonSelfDisplacingException | PersonDataInvalidValueException | PersonDataValueLengthException
            | PersonDataNameLengthException | PersonDataInvalidNameException | AuthorizationException
            | PersonDisplacedIdentityException | PersonDisplacedMissingIdentityException
            | ShareableDataAttributeNameInvalidException | ShareableDataAttributeValueInvalidException
            | ShareableDataAttributeNameLengthException | ShareableDataAttributeValueLengthException
            | ShareableLabelIllegalCharacterInNameException | ShareableLabelNameLengthException
            | ShareableContentDescriptionTooLongException | ShareableBlockedUrlException | ShareableFieldLengthException
            | ShareableFieldValueException | ShareableCodeTakenException | ShareableCodeTakenByPromotionException
            | ShareableCodeReservedException | ShareableNotFoundException | ShareableProgramNotFoundException
            | ReadOnlyPersonDataException e) {
            throw new LockClosureException(e);
        }
    }

    public static PersonBuilder createPerson(PersonBuilderContext personBuilderContext) {
        return new PersonBuilderImpl(
            personBuilderContext.getSandboxService(),
            personBuilderContext.getClientId(),
            personBuilderContext.getVerifiedEmailService(),
            personBuilderContext.getPersonService(),
            personBuilderContext.getBackendAuthorizationProvider(),
            personBuilderContext.getProgramDomainCache(),
            personBuilderContext.getPersonOperations(),
            personBuilderContext.getClientDomainId(),
            Optional.empty());
    }

    public static PersonBuilder updatePerson(PersonBuilderContext personBuilderContext, Id<PersonHandle> personId) {
        return new PersonBuilderImpl(
            personBuilderContext.getSandboxService(),
            personBuilderContext.getClientId(),
            personBuilderContext.getVerifiedEmailService(),
            personBuilderContext.getPersonService(),
            personBuilderContext.getBackendAuthorizationProvider(),
            personBuilderContext.getProgramDomainCache(),
            personBuilderContext.getPersonOperations(),
            personBuilderContext.getClientDomainId(),
            Optional.of(personId));
    }

    private static final class PersonBlock {
        private final String message;

        private PersonBlock(String message) {
            this.message = message;
        }

        private String getMessage() {
            return message;
        }
    }
}
