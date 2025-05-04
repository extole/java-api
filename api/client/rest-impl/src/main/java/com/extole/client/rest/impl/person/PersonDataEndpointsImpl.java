package com.extole.client.rest.impl.person;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonDataCreateRequest;
import com.extole.client.rest.person.PersonDataEndpoints;
import com.extole.client.rest.person.PersonDataListRequest;
import com.extole.client.rest.person.PersonDataResponse;
import com.extole.client.rest.person.PersonDataRestException;
import com.extole.client.rest.person.PersonDataScope;
import com.extole.client.rest.person.PersonDataUpdateRequest;
import com.extole.client.rest.person.PersonDataValidationRestException;
import com.extole.client.rest.person.PersonRestException;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.id.Id;
import com.extole.person.service.profile.FullPersonService;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonData;
import com.extole.person.service.profile.PersonDataAlreadyExistsException;
import com.extole.person.service.profile.PersonDataBuilder;
import com.extole.person.service.profile.PersonDataInvalidNameException;
import com.extole.person.service.profile.PersonDataInvalidValueException;
import com.extole.person.service.profile.PersonDataNameLengthException;
import com.extole.person.service.profile.PersonDataNotFoundException;
import com.extole.person.service.profile.PersonDataQueryBuilder;
import com.extole.person.service.profile.PersonDataValueLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ReadOnlyPersonDataException;

@Provider
public class PersonDataEndpointsImpl implements PersonDataEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final FullPersonService fullPersonService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonDataEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PersonService personService,
        FullPersonService fullPersonService,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.personService = personService;
        this.fullPersonService = fullPersonService;
        this.authorizationProvider = authorizationProvider;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public PersonDataResponse get(String accessToken, String personId, String name, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonDataRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Optional<PersonData> personData = Optional.ofNullable(
                personService.getPerson(authorization, Id.valueOf(personId)).getAllData().get(name));
            if (personData.isEmpty()) {
                personData = fullPersonService.createDataQueryBuilder(authorization, Id.valueOf(personId))
                    .withNames(List.of(name)).withLimit(1)
                    .list().stream()
                    .findFirst();
            }
            return personData.map(data -> mapToResponse(timeZone, data))
                .orElseThrow(() -> RestExceptionBuilder.newBuilder(PersonDataRestException.class)
                    .withErrorCode(PersonDataRestException.PERSON_DATA_NOT_FOUND)
                    .addParameter("name", name)
                    .build());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId).withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public Map<String, PersonDataResponse> list(String accessToken, String personId,
        PersonDataListRequest listRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            PersonDataQueryBuilder builder = fullPersonService.createDataQueryBuilder(
                userAuthorization, Id.valueOf(personId))
                .withNames(listRequest.getNames())
                .withScopes(listRequest.getScopes().stream()
                    .map(PersonDataScope::name)
                    .map(PersonData.Scope::valueOf)
                    .collect(Collectors.toList()))
                .withOffset(listRequest.getOffset()
                    .orElse(Integer.valueOf(PersonDataEndpoints.DEFAULT_OFFSET)).intValue())
                .withLimit(listRequest.getLimit()
                    .orElse(Integer.valueOf(PersonDataEndpoints.DEFAULT_LIMIT)).intValue());
            return builder.list().stream()
                .map(data -> mapToResponse(timeZone, data))
                .collect(Collectors.toMap(PersonDataResponse::getName, data -> data));
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId).withCause(e)
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        }
    }

    @Override
    public PersonDataResponse create(String accessToken, String personId,
        PersonDataCreateRequest createRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonDataRestException,
        PersonDataValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-v5-data-endpoints-add-person-data"),
                (personBuilder, originalPersonProfile) -> {
                    try {
                        if (originalPersonProfile.getData().containsKey(createRequest.getName())) {
                            throw new LockClosureException(new PersonDataAlreadyExistsException("Person data already " +
                                "exists"));
                        }
                        PersonDataBuilder builder = personBuilder.addOrReplaceData(createRequest.getName());
                        if (createRequest.getScope().isPresent()) {
                            builder.withScope(PersonData.Scope.valueOf(createRequest.getScope().getValue().name()));
                        }

                        builder.withValue(createRequest.getValue());
                        builder.validate();
                    } catch (PersonDataNameLengthException | PersonDataInvalidNameException
                        | ReadOnlyPersonDataException e) {
                        LockClosureException exception = new LockClosureException(e);
                        exception.addParameter("name", createRequest.getName());
                        throw exception;
                    } catch (PersonDataValueLengthException | PersonDataInvalidValueException e) {
                        throw new LockClosureException(e);
                    }

                    return personBuilder.save();
                }, consumerEventSenderService.createConsumerEventSender());
            sendInputEvent(authorization, person, ConsumerEventName.EXTOLE_PROFILE.getEventName());

            PersonData updatedData = person.getAllData().get(createRequest.getName());

            return mapToResponse(timeZone, updatedData);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonDataNameLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.DATA_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataValueLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.DATA_VALUE_LENGTH_OUT_OF_RANGE)
                    .withCause(cause)
                    .addParameter("value", createRequest.getValue()).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataInvalidValueException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.INVALID_VALUE).withCause(cause)
                    .addParameter("value", createRequest.getValue()).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataInvalidNameException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.INVALID_NAME)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataAlreadyExistsException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataRestException.class)
                    .withErrorCode(PersonDataRestException.DATA_ALREADY_EXISTS)
                    .addParameter("name", createRequest.getName()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ReadOnlyPersonDataException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataRestException.class)
                    .withErrorCode(PersonDataRestException.DATA_NAME_READONLY)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).addParameter("person_id", personId).withCause(e)
                .build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    @Override
    public PersonDataResponse update(String accessToken, String personId,
        String name, PersonDataUpdateRequest updateRequest, ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonDataRestException,
        PersonDataValidationRestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-v5-data-endpoints-edit-person-data"),
                (personBuilder, originalPersonProfile) -> {
                    try {
                        if (!originalPersonProfile.getData().containsKey(name)) {
                            throw new LockClosureException(new PersonDataNotFoundException("Person data not found"));
                        }
                        PersonDataBuilder builder = personBuilder.addOrReplaceData(name);
                        if (updateRequest.getScope().isPresent()) {
                            builder.withScope(PersonData.Scope.valueOf(updateRequest.getScope().getValue().name()));
                        }

                        if (updateRequest.getValue().isPresent()) {
                            builder.withValue(updateRequest.getValue().getValue());
                        }
                        builder.validate();
                    } catch (PersonDataNameLengthException | PersonDataInvalidNameException
                        | ReadOnlyPersonDataException e) {
                        LockClosureException exception = new LockClosureException(e);
                        exception.addParameter("name", name);
                        throw exception;
                    } catch (PersonDataValueLengthException | PersonDataInvalidValueException e) {
                        throw new LockClosureException(e);
                    }

                    return personBuilder.save();
                }, consumerEventSenderService.createConsumerEventSender());
            sendInputEvent(authorization, person, ConsumerEventName.EXTOLE_PROFILE.getEventName());

            PersonData updatedData = person.getAllData().get(name);

            return mapToResponse(timeZone, updatedData);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonDataNameLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.DATA_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataValueLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.DATA_VALUE_LENGTH_OUT_OF_RANGE)
                    .withCause(cause)
                    .addParameter("value", updateRequest.getValue()).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataInvalidValueException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.INVALID_VALUE).withCause(cause)
                    .addParameter("value", updateRequest.getValue()).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataInvalidNameException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataValidationRestException.class)
                    .withErrorCode(PersonDataValidationRestException.INVALID_NAME)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonDataNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataRestException.class)
                    .withErrorCode(PersonDataRestException.PERSON_DATA_NOT_FOUND)
                    .addParameter("name", name).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ReadOnlyPersonDataException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonDataRestException.class)
                    .withErrorCode(PersonDataRestException.DATA_NAME_READONLY)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND).addParameter("person_id", personId).withCause(e)
                .build();
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e).build();
        }
    }

    private void sendInputEvent(ClientAuthorization authorization, Person person, String eventName)
        throws EventProcessorException, AuthorizationException {
        ProcessedRawEvent processedRawEvent = clientRequestContextService.createBuilder(authorization, servletRequest)
            .withEventName(eventName)
            .withHttpRequestBodyCapturing(ClientRequestContextService.HttpRequestBodyCapturingType.LIMITED)
            .build().getProcessedRawEvent();
        consumerEventSenderService.createInputEvent(authorization, processedRawEvent, person).send();
    }

    private PersonDataResponse mapToResponse(ZoneId timeZone, PersonData data) {
        return new PersonDataResponse(
            data.getName(),
            PersonDataScope.valueOf(data.getScope().name()),
            data.getValue(),
            data.getCreatedDate().atZone(timeZone),
            data.getUpdatedDate().atZone(timeZone));
    }
}
