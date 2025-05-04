package com.extole.client.rest.impl.person.v2;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.v2.PersonParametersBulkUpdateV2Request;
import com.extole.client.rest.person.v2.PersonParametersUpdateV2Request;
import com.extole.client.rest.person.v2.PersonParametersV2Endpoints;
import com.extole.client.rest.person.v2.PersonParametersV2RestException;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonData;
import com.extole.person.service.profile.PersonDataBuilder;
import com.extole.person.service.profile.PersonDataInvalidNameException;
import com.extole.person.service.profile.PersonDataInvalidValueException;
import com.extole.person.service.profile.PersonDataNameLengthException;
import com.extole.person.service.profile.PersonDataValueLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ReadOnlyPersonDataException;

@Provider
public class PersonParametersV2EndpointsImpl implements PersonParametersV2Endpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public PersonParametersV2EndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        PersonService personService,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.personService = personService;
        this.authorizationProvider = authorizationProvider;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public Map<String, Object> getPersonProfileParameters(String accessToken, String personId)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return personService.getPerson(authorization, Id.valueOf(personId)).getData();
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
    public SuccessResponse editPersonProfileParameters(String accessToken,
        String personId, PersonParametersBulkUpdateV2Request bulkUpdateRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonParametersV2RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Person person = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-data-endpoints-edit-person-profile-data"),
                (personBuilder, originalPersonProfile) -> {
                    PersonData.Scope scope;
                    if (bulkUpdateRequest.getScope() == null) {
                        scope = PersonData.Scope.PRIVATE;
                    } else {
                        scope = PersonData.Scope.valueOf(bulkUpdateRequest.getScope().name());
                    }

                    for (Entry<String, Object> requestData : bulkUpdateRequest.getData().entrySet()) {
                        try {
                            PersonDataBuilder builder =
                                personBuilder.addOrReplaceData(requestData.getKey()).withScope(scope);

                            if (requestData.getValue() != null) {
                                builder.withValue(requestData.getValue());
                            }
                            builder.validate();
                        } catch (PersonDataNameLengthException | PersonDataInvalidNameException
                            | ReadOnlyPersonDataException e) {
                            LockClosureException exception = new LockClosureException(e);
                            exception.addParameter("name", requestData.getKey());
                            throw exception;
                        } catch (PersonDataValueLengthException
                            | PersonDataInvalidValueException e) {
                            throw new LockClosureException(e);
                        }
                    }

                    return personBuilder.save();
                }, consumerEventSenderService.createConsumerEventSender());
            sendInputEvent(authorization, person, ConsumerEventName.EXTOLE_PROFILE.getEventName());
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonDataNameLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.CLIENT_PARAMS_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataValueLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.CLIENT_PARAMS_VALUE_LENGTH_OUT_OF_RANGE)
                    .withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataInvalidValueException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.INVALID_VALUE).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataInvalidNameException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.INVALID_NAME)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(ReadOnlyPersonDataException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.READ_ONLY_NAME)
                    .addParameter("name", e.getParameter("name")).withCause(cause).build();
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
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

        return SuccessResponse.getInstance();
    }

    @Override
    public SuccessResponse putPersonProfileParameter(String accessToken, String personId, String parameterName,
        PersonParametersUpdateV2Request parameterRequest)
        throws UserAuthorizationRestException, PersonRestException, PersonParametersV2RestException {

        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        PersonData.Scope scope;
        if (parameterRequest.getScope() != null) {
            scope = PersonData.Scope.valueOf(parameterRequest.getScope().name());
        } else {
            scope = PersonData.Scope.PRIVATE;
        }

        try {
            Person person = personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("person-data-endpoints-put-person-profile-param"),
                (personBuilder, originalPersonProfile) -> {
                    try {
                        PersonDataBuilder builder =
                            personBuilder.addOrReplaceData(parameterName).withScope(scope);

                        if (parameterRequest.getValue() != null) {
                            builder.withValue(parameterRequest.getValue());
                        }
                        builder.validate();
                        return personBuilder.save();
                    } catch (PersonDataInvalidNameException | PersonDataInvalidValueException
                        | PersonDataValueLengthException | PersonDataNameLengthException
                        | ReadOnlyPersonDataException e) {
                        throw new LockClosureException(e);
                    }

                }, consumerEventSenderService.createConsumerEventSender());
            sendInputEvent(authorization, person, ConsumerEventName.EXTOLE_PROFILE.getEventName());
            return SuccessResponse.getInstance();
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonDataInvalidNameException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.INVALID_NAME)
                    .addParameter("name", parameterName).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(ReadOnlyPersonDataException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.READ_ONLY_NAME)
                    .addParameter("name", parameterName).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataInvalidValueException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.INVALID_VALUE).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataNameLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.CLIENT_PARAMS_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", parameterName).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataValueLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(PersonParametersV2RestException.class)
                    .withErrorCode(PersonParametersV2RestException.CLIENT_PARAMS_VALUE_LENGTH_OUT_OF_RANGE)
                    .withCause(cause).build();
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
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

}
