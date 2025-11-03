package com.extole.client.rest.impl.person;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonEndpoints;
import com.extole.client.rest.person.PersonForwardRequest;
import com.extole.client.rest.person.PersonLocaleResponse;
import com.extole.client.rest.person.PersonRequest;
import com.extole.client.rest.person.PersonResponse;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.PersonSearchRequest;
import com.extole.client.rest.person.PersonValidationRestException;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.omissible.Omissible;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.id.Id;
import com.extole.model.entity.client.PublicClient;
import com.extole.model.service.client.ClientNotFoundException;
import com.extole.model.shared.client.ClientCache;
import com.extole.person.service.identity.InvalidIdentityKeyValueException;
import com.extole.person.service.profile.ForwardToDeviceProfileException;
import com.extole.person.service.profile.ForwardingDeviceProfileException;
import com.extole.person.service.profile.IdentityKeyValueAlreadyTakenException;
import com.extole.person.service.profile.IdentityKeyValueUnauthorizedUpdateException;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonOperations;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.key.PersonKey;
import com.extole.person.service.profile.locale.PersonLocale;
import com.extole.running.service.partner.PartnerProfileKeyService;

@Provider
public class PersonEndpointsImpl implements PersonEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PersonEndpointsImpl.class);
    private static final Set<String> KNOWN_PERSON_KEYS =
        Sets.newHashSet(PersonKey.EMAIL, PersonKey.PARTNER_USER_ID, PersonKey.SHARE_ID);

    private final ClientAuthorizationProvider authorizationProvider;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final PartnerProfileKeyService partnerProfileKeyService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;
    private final ClientCache clientCache;
    private final PersonService personService;

    @Autowired
    public PersonEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        ConsumerEventSenderService consumerEventSenderService,
        PartnerProfileKeyService partnerProfileKeyService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService,
        ClientCache clientCache,
        PersonService personService) {
        this.authorizationProvider = authorizationProvider;
        this.consumerEventSenderService = consumerEventSenderService;
        this.partnerProfileKeyService = partnerProfileKeyService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
        this.clientCache = clientCache;
        this.personService = personService;
    }

    @Override
    public PersonResponse get(String accessToken, String personId)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personToResponse(personService.getPerson(authorization, Id.valueOf(personId)));
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<PersonResponse> search(String accessToken, PersonSearchRequest request)
        throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        boolean searchByIdentityKey =
            request.getIdentityKeyValue().isPresent() && !Strings.isNullOrEmpty(request.getIdentityKeyValue().get());
        boolean searchByPersonKeys = request.getPersonKeys() != null && !request.getPersonKeys().isEmpty()
            && request.getPersonKeys().stream().anyMatch(personKey -> !Strings.isNullOrEmpty(personKey));

        try {
            if (searchByIdentityKey) {
                PublicClient client = getPublicClient(authorization);
                Optional<Person> personLookupResult = personService.getPersonByProfileLookupKey(authorization,
                    PersonKey.ofType(client.getIdentityKey().getName(), request.getIdentityKeyValue().get()));
                if (personLookupResult.isPresent()) {
                    return Collections.singletonList(personToResponse(personLookupResult.get()));
                }
            } else if (searchByPersonKeys) {
                List<PersonKey> personKeys = getPersonKeys(authorization, request.getPersonKeys());

                ImmutableList.Builder<PersonResponse> personsByPersonKeyListBuilder = ImmutableList.builder();
                for (PersonKey personKey : personKeys) {
                    personService.getPersonByProfileLookupKey(authorization, personKey)
                        .ifPresent(person -> personsByPersonKeyListBuilder.add(personToResponse(person)));
                }
                return subList(personsByPersonKeyListBuilder.build(), request.getOffset(), request.getLimit());
            } else {
                LOG.debug("No search criteria provided, returning empty result");
                return Collections.emptyList();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
        return Collections.emptyList();
    }

    @Override
    public PersonResponse create(String accessToken, PersonRequest personRequest)
        throws UserAuthorizationRestException, PersonValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Omissible<String> identityKeyValue = personRequest.getIdentityKeyValue();
            PersonOperations personOperations = consumerEventSenderService.createConsumerEventSender()
                .log("New person created via client person endpoints");
            Person updatedPerson =
                personService.newPerson(authorization,
                    new LockDescription("person-endpoints-create"), personBuilder -> {
                        try {
                            if (identityKeyValue.isPresent()
                                && !Strings.isNullOrEmpty(identityKeyValue.getValue())) {
                                personBuilder.withIdentityKeyValue(identityKeyValue.getValue());
                            }
                            return personBuilder.save();
                        } catch (IdentityKeyValueUnauthorizedUpdateException | IdentityKeyValueAlreadyTakenException
                            | InvalidIdentityKeyValueException e) {
                            throw new LockClosureException(e);
                        }
                    }, personOperations);
            // ENG-19642 we cannot send an input event when the person is not created yet
            sendEvents(authorization, updatedPerson);
            return personToResponse(updatedPerson);
        } catch (LockClosureException e) {
            return handlePersonUpdateLockClosureException(personRequest, e);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EventProcessorException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public PersonResponse update(String accessToken, String personId, PersonRequest personRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Omissible<String> identityKeyValue = personRequest.getIdentityKeyValue();
            PersonOperations personOperations =
                consumerEventSenderService.createConsumerEventSender()
                    .log("Person updated via client person endpoints." + " Person id: " + personId);
            Person updatedPerson = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-endpoints-update"),
                (personBuilder, initialPerson) -> {
                    try {
                        if (identityKeyValue.isPresent() && !Strings.isNullOrEmpty(identityKeyValue.getValue())) {
                            personBuilder.withIdentityKeyValue(identityKeyValue.getValue());
                            personOperations.log("Person key " + initialPerson.getIdentityKey() + " updated: "
                                + identityKeyValue);
                        }
                        return personBuilder.save();
                    } catch (InvalidIdentityKeyValueException | IdentityKeyValueUnauthorizedUpdateException
                        | IdentityKeyValueAlreadyTakenException e) {
                        throw new LockClosureException(e);
                    }
                }, personOperations);
            // ENG-19642 sending the input event separately just to have the same syntax as in the update method
            sendEvents(authorization, updatedPerson);
            return personToResponse(updatedPerson);
        } catch (LockClosureException e) {
            return handlePersonUpdateLockClosureException(personRequest, e);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public PersonResponse forward(String accessToken, String personId, PersonForwardRequest personForwardRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Id<?> forwardToProfileId = Id.valueOf(defaultIfBlank(personForwardRequest.getForwardToProfileId(), ""));

        try {
            PersonOperations personOperations =
                consumerEventSenderService.createConsumerEventSender()
                    .log("Person forward via client person endpoints. Person id: " + personId
                        + " forward to person id: " + forwardToProfileId);

            Person person = personService.getPerson(authorization, Id.valueOf(forwardToProfileId.getValue()));
            Person updatedPerson = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-endpoints-forward"),
                (personBuilder, initialPerson) -> {
                    if (person.getIdentityKeyValue().isPresent()) {
                        try {
                            personBuilder.withForwardToProfileId(forwardToProfileId);
                            personBuilder.withIdentityKeyValue(person.getIdentityKeyValue().get());
                        } catch (InvalidIdentityKeyValueException | IdentityKeyValueUnauthorizedUpdateException
                            | IdentityKeyValueAlreadyTakenException | ForwardingDeviceProfileException
                            | PersonNotFoundException | ForwardToDeviceProfileException e) {
                            throw new LockClosureException(e);
                        }
                    }
                    return personBuilder.save();
                }, personOperations);

            // ENG-19642 sending the input event separately just to have the same syntax as in the update method
            sendEvents(authorization, updatedPerson);
            return personToResponse(updatedPerson);
        } catch (LockClosureException e) {
            return handlePersonForwardLockClosureException(e);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private PersonResponse handlePersonUpdateLockClosureException(PersonRequest personRequest, LockClosureException e)
        throws PersonValidationRestException {
        Throwable cause = e.getCause();
        if (cause instanceof IdentityKeyValueAlreadyTakenException) {
            throw RestExceptionBuilder.newBuilder(PersonValidationRestException.class)
                .withErrorCode(PersonValidationRestException.IDENTITY_KEY_VALUE_ALREADY_TAKEN)
                .addParameter("identity_key_value", personRequest.getIdentityKeyValue()).withCause(cause)
                .build();
        }
        if (cause instanceof InvalidIdentityKeyValueException) {
            throw RestExceptionBuilder.newBuilder(PersonValidationRestException.class)
                .withErrorCode(PersonValidationRestException.IDENTITY_KEY_VALUE_INVALID)
                .addParameter("identity_key_value", personRequest.getIdentityKeyValue())
                .withCause(e).build();
        }
        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
    }

    private PersonResponse handlePersonForwardLockClosureException(LockClosureException e)
        throws PersonValidationRestException, PersonRestException {
        Throwable cause = e.getCause();
        if (cause instanceof PersonNotFoundException) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", ((PersonNotFoundException) cause).getPersonId())
                .withCause(e).build();
        }
        if (cause instanceof ForwardingDeviceProfileException) {
            throw RestExceptionBuilder.newBuilder(PersonValidationRestException.class)
                .withErrorCode(PersonValidationRestException.FORWARDING_PROFILE_IS_DEVICE)
                .addParameter("profile_id", ((ForwardingDeviceProfileException) cause).getProfileId())
                .withCause(e).build();
        }
        if (cause instanceof ForwardToDeviceProfileException) {
            throw RestExceptionBuilder.newBuilder(PersonValidationRestException.class)
                .withErrorCode(PersonValidationRestException.FORWARD_TO_PROFILE_IS_DEVICE)
                .addParameter("profile_id", ((ForwardToDeviceProfileException) cause).getProfileId())
                .withCause(e).build();
        }
        throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
            .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
            .withCause(cause)
            .build();
    }

    private List<PersonKey> getPersonKeys(Authorization authorization, List<String> personKeys) {
        Set<String> recognizedKeyTypes = Sets.newHashSet();
        recognizedKeyTypes.addAll(getRecognizedDynamicKeyTypes(authorization));
        recognizedKeyTypes.addAll(KNOWN_PERSON_KEYS);

        return personKeys.stream()
            .map(personKey -> Pair.of(StringUtils.substringBefore(personKey, ":"),
                StringUtils.substringAfter(personKey, ":")))
            .filter(pair -> StringUtils.isNotBlank(pair.getLeft()))
            .filter(pair -> StringUtils.isNotBlank(pair.getRight()))
            .filter(pair -> recognizedKeyTypes.contains(pair.getKey().trim().toUpperCase()))
            .map(pair -> PersonKey.ofType(pair.getKey(), pair.getValue()))
            .collect(Collectors.toList());
    }

    private Set<String> getRecognizedDynamicKeyTypes(Authorization authorization) {
        return partnerProfileKeyService.getPartnerProfileKeyNames(authorization.getClientId()).stream()
            .map(String::toUpperCase)
            .collect(Collectors.toSet());
    }

    private void sendEvents(ClientAuthorization authorization, Person person)
        throws EventProcessorException, AuthorizationException, PersonNotFoundException {
        ProcessedRawEvent processedRawEvent = clientRequestContextService.createBuilder(authorization, servletRequest)
            .withEventName(ConsumerEventName.EXTOLE_PROFILE.getEventName())
            .withHttpRequestBodyCapturing(ClientRequestContextService.HttpRequestBodyCapturingType.LIMITED)
            .build().getProcessedRawEvent();
        consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person.getId()).send();
    }

    private PersonResponse personToResponse(Person person) {
        return new PersonResponse(
            person.getId().getValue(),
            person.hasIdentity() ? Optional.of(person.getIdentityId().getValue()) : Optional.empty(),
            person.getIdentityKey(),
            person.getIdentityKeyValue(),
            toPersonLocaleResponse(person.getLocale()),
            person.getVersion());
    }

    private PersonLocaleResponse toPersonLocaleResponse(PersonLocale locale) {
        return new PersonLocaleResponse(locale.getLastBrowser(), locale.getUserSpecified());
    }

    private List<PersonResponse> subList(List<PersonResponse> persons, int offset, int limit) {
        if (offset < 0 || limit < 1) {
            return Collections.emptyList();
        }

        int toIndex = offset + limit;

        if (offset >= persons.size()) {
            return Collections.emptyList();
        }

        if (toIndex > persons.size()) {
            toIndex = persons.size();
        }

        return persons.subList(offset, toIndex);
    }

    private PublicClient getPublicClient(Authorization authorization) throws AuthorizationException {
        try {
            return clientCache.getById(authorization.getClientId());
        } catch (ClientNotFoundException e) {
            throw new AuthorizationException("Cannot find client ", e);
        }
    }
}
