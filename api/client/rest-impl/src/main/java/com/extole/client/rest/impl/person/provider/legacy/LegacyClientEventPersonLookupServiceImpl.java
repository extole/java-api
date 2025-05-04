package com.extole.client.rest.impl.person.provider.legacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.actions.Action;
import com.extole.actions.ActionService;
import com.extole.actions.ActionType;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.metrics.ExtoleMetricRegistry;
import com.extole.consumer.event.service.ConsumerEventSender;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmail;
import com.extole.email.provider.service.VerifiedEmailService;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonEmailAlreadyDefinedException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.key.PersonKey;

@Deprecated // TODO completely remove in ENG-12938
@Component
public class LegacyClientEventPersonLookupServiceImpl implements LegacyClientEventPersonLookupService {

    private static final Logger LOG = LoggerFactory.getLogger(LegacyClientEventPersonLookupService.class);

    private final ActionService actionService;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final VerifiedEmailService verifiedEmailService;
    private final ExtoleMetricRegistry metricRegistry;

    @Autowired
    public LegacyClientEventPersonLookupServiceImpl(
        ActionService actionService,
        PersonService personService,
        ConsumerEventSenderService consumerEventSenderService,
        VerifiedEmailService verifiedEmailService,
        ExtoleMetricRegistry metricRegistry) {
        this.actionService = actionService;
        this.personService = personService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.verifiedEmailService = verifiedEmailService;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public LegacyClientEventPersonLookupBuilder newLookup(ClientAuthorization authorization) {
        return new LegacyClientEventPersonLookupBuilderImpl(authorization);
    }

    @Nullable
    private Person lookupByPersonId(ClientAuthorization authorization, @Nullable String personId)
        throws AuthorizationException, PersonNotFoundException {
        if (personId == null) {
            return null;
        }
        return personService.getPerson(authorization, Id.valueOf(personId));
    }

    @Nullable
    private Person lookupByAction(ClientAuthorization authorization, @Nullable Action action)
        throws PersonNotFoundException, AuthorizationException {
        if (action == null) {
            return null;
        }
        String personId = Optional.ofNullable(action.getPersonId()).map(Id::getValue).orElse(null);
        return lookupByPersonId(authorization, personId);
    }

    @Nullable
    private Person lookupByEmail(ClientAuthorization authorization, @Nullable String email)
        throws AuthorizationException {
        if (email == null) {
            return null;
        }
        Optional<Person> person =
            personService.getPersonByProfileLookupKey(authorization, PersonKey.ofEmailType(email));
        return person.orElse(null);
    }

    @Nullable
    private Person lookupByPartnerConversionId(ClientAuthorization authorization, @Nullable String partnerConversionId)
        throws AuthorizationException {
        if (partnerConversionId == null) {
            return null;
        }

        Optional<Action> registration = actionService.findLatestByActionTypeAndPartnerConversionId(authorization,
            ActionType.REGISTER, partnerConversionId);
        if (registration.isPresent() && registration.get().getPersonId() != null) {
            try {
                return personService.getPerson(authorization, registration.get().getPersonId());
            } catch (PersonNotFoundException ignored) {
                // ignored
            }
        }

        Optional<Action> click = actionService.findLatestByActionTypeAndPartnerConversionId(authorization,
            ActionType.CLICK, partnerConversionId);
        if (click.isPresent() && click.get().getPersonId() != null) {
            try {
                return personService.getPerson(authorization, click.get().getPersonId());
            } catch (PersonNotFoundException ignored) {
                // ignored
            }
        }

        Optional<Action> purchase = actionService.findLatestByActionTypeAndPartnerConversionId(authorization,
            ActionType.PURCHASE, partnerConversionId);
        if (purchase.isPresent() && purchase.get().getPersonId() != null) {
            try {
                return personService.getPerson(authorization, purchase.get().getPersonId());
            } catch (PersonNotFoundException ignored) {
                // ignored
            }
        }

        return null;
    }

    @Nullable
    private Person lookupByPartnerUserId(ClientAuthorization authorization, @Nullable String partnerUserId)
        throws AuthorizationException {
        if (partnerUserId == null) {
            return null;
        }
        return personService.getPersonByProfileLookupKey(authorization, PersonKey.ofPartnerUserIdType(partnerUserId))
            .orElse(null);
    }

    @Nullable
    private Person lookupByPersonKey(ClientAuthorization authorization, PersonKey personKey)
        throws AuthorizationException {
        return personService.getPersonByProfileLookupKey(authorization, personKey).orElse(null);
    }

    public final class LegacyClientEventPersonLookupBuilderImpl implements LegacyClientEventPersonLookupBuilder {

        private final ClientAuthorization authorization;
        @Nullable
        private String personId;
        @Nullable
        private String email;
        @Nullable
        private String partnerUserId;
        @Nullable
        private String partnerConversionId;
        @Nullable
        private Action action;
        private Set<PersonKey> personKeys = Sets.newHashSet();
        @Nullable
        private PublicProgram clientDomain;

        private LegacyClientEventPersonLookupBuilderImpl(ClientAuthorization authorization) {
            this.authorization = authorization;
        }

        @Override
        public LegacyClientEventPersonLookupBuilder withPersonId(@Nullable String personId) {
            this.personId = Strings.emptyToNull(personId);
            return this;
        }

        @Override
        public LegacyClientEventPersonLookupBuilder withEmail(@Nullable String email) {
            this.email = Strings.emptyToNull(email);
            return this;
        }

        @Override
        public LegacyClientEventPersonLookupBuilder withPartnerUserId(@Nullable String partnerUserId) {
            this.partnerUserId = Strings.emptyToNull(partnerUserId);
            return this;
        }

        @Deprecated // TODO REMOVE IN ENG-10566
        @Override
        public LegacyClientEventPersonLookupBuilder withPartnerConversionId(@Nullable String partnerConversionId) {
            this.partnerConversionId = Strings.emptyToNull(partnerConversionId);
            return this;
        }

        @Deprecated // TODO remove in ENG-8070
        public LegacyClientEventPersonLookupBuilder withAction(@Nullable Action action) {
            this.action = action;
            return this;
        }

        @Override
        public LegacyClientEventPersonLookupBuilder withPersonKeys(Set<PersonKey> personKeys) {
            this.personKeys = ImmutableSet.copyOf(personKeys);
            return this;
        }

        @Override
        public LegacyClientEventPersonLookupBuilder withClientDomain(@Nullable PublicProgram clientDomain) {
            this.clientDomain = clientDomain;
            return this;
        }

        @Override
        public LegacyClientEventPersonLookupResult lookup() throws AuthorizationException, PersonNotFoundException {
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                Person person = lookupByPersonId(authorization, personId);
                String note = "Person identified by person_id=" + personId;
                if (person == null && !Strings.isNullOrEmpty(email)) {
                    person = lookupByEmail(authorization, email);
                    note = "Person identified by email=" + email;
                }
                if (person == null) {
                    person = lookupByPartnerUserId(authorization, partnerUserId);
                    note = "Person identified by partner_user_id=" + partnerUserId;
                }
                for (Iterator<PersonKey> iterator = personKeys.iterator(); iterator.hasNext() && person == null;) {
                    PersonKey personKey = iterator.next();
                    person = lookupByPersonKey(authorization, personKey);
                    note = "Person identified by personKey=" + personKey;
                }
                if (person == null) {
                    person = lookupByPartnerConversionId(authorization, partnerConversionId);
                    note = "Person identified by partner_conversion_id=" + partnerConversionId;
                }
                if (person == null) {
                    person = lookupByAction(authorization, action);
                    note = "Person identified by click action=" + (action != null ? action.getActionId() : "");
                }
                if (person == null) {
                    return new LegacyClientEventPersonLookupResultImpl(Collections.emptyList());
                }
                List<String> logMessages = new ArrayList<>();
                logMessages.add(note);
                logMessages.addAll(checkPartnerUserIdMismatchAndReturnLogMessages(person));
                return new LegacyClientEventPersonLookupResultImpl(person, logMessages);
            } finally {
                stopwatch.stop();
                ClientEventPersonLookupMetrics.PERSON_LOOKUP_DURATION.updateHistogram(metricRegistry,
                    stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        }

        public LegacyClientEventPersonLookupOrCreateResult create() {
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                Person person = null;
                List<String> logMessages = new ArrayList<>();
                if (email != null) {
                    try {
                        VerifiedEmail verifiedEmail = verifiedEmailService.verifyEmail(email);
                        ConsumerEventSender consumerEventSender =
                            consumerEventSenderService.createConsumerEventSender();
                        if (clientDomain != null) {
                            consumerEventSender.withClientDomainContext(new ClientDomainContext(
                                clientDomain.getProgramDomain().toString(), clientDomain.getId()));
                        }
                        person = personService.newPerson(authorization,
                            new LockDescription("client-person-lookup-identify"), personBuilder -> {
                                try {
                                    return personBuilder.withEmail(verifiedEmail.getEmail()).save();
                                } catch (PersonEmailAlreadyDefinedException e) {
                                    throw new LockClosureException(e);
                                }
                            }, consumerEventSender);
                        logMessages.add("Person created on lookup by email via client API. Email: " + email);
                    } catch (LockClosureException | InvalidEmailAddress | InvalidEmailDomainException e) {
                        String errorMessage = "Failed to create new person with email: " + email + " for client: " +
                            authorization.getClientId();
                        LOG.warn(errorMessage, e);
                        logMessages.add(errorMessage);
                    }
                }
                if (person == null) {
                    person = personService.newPerson(authorization.getClientId());
                    logMessages.add("New anonymous person created " + person.getId());
                }
                return new LegacyClientEventPersonLookupOrCreateResultImpl(person, logMessages);
            } finally {
                stopwatch.stop();
                ClientEventPersonLookupMetrics.PERSON_CREATE_DURATION.updateHistogram(metricRegistry,
                    stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        }

        @Override
        public LegacyClientEventPersonLookupOrCreateResult lookupOrCreate() throws AuthorizationException,
            PersonNotFoundException {
            LegacyClientEventPersonLookupResult lookupResult = lookup();
            if (lookupResult.getPerson().isPresent()) {
                return new LegacyClientEventPersonLookupOrCreateResultImpl(lookupResult.getPerson().get(),
                    lookupResult.getLogMessages());
            }
            return create();
        }

        private List<String> checkPartnerUserIdMismatchAndReturnLogMessages(Person person) {
            List<String> logMessages = new ArrayList<>();
            if (partnerUserId != null && person.getPartnerUserId() != null
                && !person.getPartnerUserId().equalsIgnoreCase(partnerUserId)) {
                String note = String.format(
                    "event partner_user_id=%s contradicts with person partner_user_id=%s (personId=%s clientId=%s)",
                    partnerUserId, person.getPartnerUserId(), person.getId(), person.getClientId());
                LOG.warn(note);
                logMessages.add(note);
            }
            return logMessages;
        }
    }

}
