package com.extole.consumer.rest.impl.me;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.PollingStatus;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.MeDataBulkUpdateRequest;
import com.extole.consumer.rest.me.MeDataEndpoints;
import com.extole.consumer.rest.me.MeDataRestException;
import com.extole.consumer.rest.me.MeDataUpdateRequest;
import com.extole.consumer.rest.me.UpdateProfileResponse;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.id.TypedIdGenerator;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonData;
import com.extole.person.service.profile.PersonDataBuilder;
import com.extole.person.service.profile.PersonDataInvalidNameException;
import com.extole.person.service.profile.PersonDataInvalidValueException;
import com.extole.person.service.profile.PersonDataNameLengthException;
import com.extole.person.service.profile.PersonDataValueLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.ReadOnlyPersonDataException;

@Provider
public class MeDataEndpointsImpl implements MeDataEndpoints {
    private static final TypedIdGenerator ID_GENERATOR = new TypedIdGenerator();

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerEventSenderService consumerEventSenderService;

    @Inject
    public MeDataEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        ConsumerEventSenderService consumerEventSenderService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.consumerEventSenderService = consumerEventSenderService;
    }

    @Override
    public Map<String, Object> getPersonProfileParameters(String accessToken) throws AuthorizationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        return authorization.getIdentity().getData();
    }

    @Override
    public UpdateProfileResponse editPersonProfileParameters(String accessToken, MeDataBulkUpdateRequest request)
        throws MeDataRestException, AuthorizationRestException {

        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withEventName(ConsumerEventName.EXTOLE_PROFILE_DATA.getEventName())
            .withEventProcessing(configurator -> {
                request.getData().forEach((key, value) -> {
                    configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                });
            })
            .build();

        PersonData.Scope scope;
        if (request.getType() == null) {
            scope = PersonData.Scope.PRIVATE;
        } else {
            scope = PersonData.Scope.valueOf(request.getType().name());
        }

        try {
            consumerEventSenderService
                .createInputEvent(requestContext.getAuthorization(), requestContext.getProcessedRawEvent())
                .withLockDescription(new LockDescription("me-data-endpoints-edit"))
                .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                    if (request.getData().isEmpty()) {
                        return new InputEventLockClosureResult<>(person);
                    }
                    for (Entry<String, Object> requestData : request.getData().entrySet()) {
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
                        } catch (PersonDataValueLengthException | PersonDataInvalidValueException e) {
                            throw new LockClosureException(e);
                        }
                    }
                    Person updatedPerson = personBuilder.save();
                    return new InputEventLockClosureResult<>(updatedPerson);
                });
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonDataNameLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.CLIENT_PARAMS_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", e.getParameter("name"))
                    .withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataValueLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.CLIENT_PARAMS_VALUE_LENGTH_OUT_OF_RANGE)
                    .withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataInvalidValueException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.INVALID_VALUE).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataInvalidNameException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.INVALID_NAME)
                    .addParameter("name", e.getParameter("name"))
                    .withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(ReadOnlyPersonDataException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.READ_ONLY_NAME)
                    .addParameter("name", e.getParameter("name"))
                    .withCause(cause).build();
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (AuthorizationException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e.getCause())
                .build();
        }

        // generating a fake pollingId
        return new UpdateProfileResponse(ID_GENERATOR.generateId().getValue(), PollingStatus.SUCCEEDED);
    }

    @Override
    public UpdateProfileResponse putPersonProfileParameter(String accessToken, String parameterName,
        MeDataUpdateRequest request) throws AuthorizationRestException, MeDataRestException {

        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withEventName(ConsumerEventName.EXTOLE_PROFILE_DATA.getEventName())
            .withEventProcessing(configurator -> {
                configurator.addData(
                    new EventData(parameterName, request.getValue(), EventData.Source.REQUEST_BODY, false, true));
            })
            .build();

        PersonData.Scope scope;
        if (request.getType() != null) {
            scope = PersonData.Scope.valueOf(request.getType().name());
        } else {
            scope = PersonData.Scope.PRIVATE;
        }

        try {
            consumerEventSenderService
                .createInputEvent(requestContext.getAuthorization(), requestContext.getProcessedRawEvent())
                .withLockDescription(new LockDescription("me-data-endpoints-put"))
                .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                    try {
                        PersonDataBuilder builder = personBuilder.addOrReplaceData(parameterName).withScope(scope);
                        if (request.getValue() != null) {
                            builder.withValue(request.getValue());
                        }
                        builder.validate();
                        Person updatedPerson = personBuilder.save();
                        return new InputEventLockClosureResult<>(updatedPerson);
                    } catch (PersonDataInvalidNameException | PersonDataInvalidValueException
                        | PersonDataValueLengthException | PersonDataNameLengthException
                        | ReadOnlyPersonDataException e) {
                        throw new LockClosureException(e);
                    }
                });
            return new UpdateProfileResponse(ID_GENERATOR.generateId().getValue(), PollingStatus.SUCCEEDED);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonDataInvalidNameException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.INVALID_NAME)
                    .addParameter("name", parameterName)
                    .withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataInvalidValueException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.INVALID_VALUE).withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataNameLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.CLIENT_PARAMS_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", parameterName)
                    .withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(PersonDataValueLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.CLIENT_PARAMS_VALUE_LENGTH_OUT_OF_RANGE)
                    .withCause(cause).build();
            }
            if (cause.getClass().isAssignableFrom(ReadOnlyPersonDataException.class)) {
                throw RestExceptionBuilder.newBuilder(MeDataRestException.class)
                    .withErrorCode(MeDataRestException.READ_ONLY_NAME)
                    .addParameter("name", e.getParameter("name"))
                    .withCause(cause).build();
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (AuthorizationException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

}
