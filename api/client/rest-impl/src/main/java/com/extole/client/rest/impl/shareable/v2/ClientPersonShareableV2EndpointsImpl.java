package com.extole.client.rest.impl.shareable.v2;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.client.ClientAuthorization;
import com.extole.client.consumer.event.service.event.context.ClientRequestContext;
import com.extole.client.consumer.event.service.event.context.ClientRequestContextService;
import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.shareable.v2.ClientPersonShareableV2Endpoints;
import com.extole.client.rest.shareable.v2.ClientShareableCreateV2RestException;
import com.extole.client.rest.shareable.v2.ClientShareableV2RestException;
import com.extole.client.rest.shareable.v2.ClientShareableValidationV2RestException;
import com.extole.client.rest.shareable.v2.ConsumerEventV2Request;
import com.extole.client.rest.shareable.v2.CreateShareableV2Request;
import com.extole.client.rest.shareable.v2.ShareableV2Content;
import com.extole.client.rest.shareable.v2.ShareableV2Response;
import com.extole.client.rest.shareable.v2.UpdateShareableV2Request;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.common.security.HashAlgorithm;
import com.extole.consumer.event.service.ConsumerEventSender;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.EventProcessorException;
import com.extole.event.consumer.ClientDomainContext;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.model.service.program.ProgramNotFoundException;
import com.extole.model.service.shareable.ClientShareable;
import com.extole.model.service.shareable.ClientShareableBuilder;
import com.extole.model.service.shareable.ClientShareableService;
import com.extole.model.service.shareable.MalformedTargetUrlException;
import com.extole.model.shared.program.ProgramDomainCache;
import com.extole.person.service.ProgramHandle;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonBuilder;
import com.extole.person.service.profile.PersonData.Scope;
import com.extole.person.service.profile.PersonDataInvalidNameException;
import com.extole.person.service.profile.PersonDataInvalidValueException;
import com.extole.person.service.profile.PersonDataNameLengthException;
import com.extole.person.service.profile.PersonDataValueLengthException;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.profile.ReadOnlyPersonDataException;
import com.extole.person.service.shareable.PersonNotRewardableException;
import com.extole.person.service.shareable.ShareableBlockedUrlException;
import com.extole.person.service.shareable.ShareableCodeReservedException;
import com.extole.person.service.shareable.ShareableCodeTakenByPromotionException;
import com.extole.person.service.shareable.ShareableCodeTakenException;
import com.extole.person.service.shareable.ShareableContent;
import com.extole.person.service.shareable.ShareableContentDescriptionTooLongException;
import com.extole.person.service.shareable.ShareableDataAttributeNameInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeNameLengthException;
import com.extole.person.service.shareable.ShareableDataAttributeValueInvalidException;
import com.extole.person.service.shareable.ShareableDataAttributeValueLengthException;
import com.extole.person.service.shareable.ShareableFieldLengthException;
import com.extole.person.service.shareable.ShareableFieldValueException;
import com.extole.person.service.shareable.ShareableKeyTakenException;
import com.extole.person.service.shareable.ShareableLabelIllegalCharacterInNameException;
import com.extole.person.service.shareable.ShareableLabelNameLengthException;
import com.extole.person.service.shareable.ShareableMissingProgramIdException;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableProgramNotFoundException;
import com.extole.person.service.shareable.ShareableTargetUrlTooLongException;

@Deprecated // TODO remove client-shareable-v2 ENG-10128
@Provider
public class ClientPersonShareableV2EndpointsImpl implements ClientPersonShareableV2Endpoints {

    private static final Logger LOG = LoggerFactory.getLogger(ClientPersonShareableV2EndpointsImpl.class);

    private final PersonService personService;
    private final ProgramDomainCache programCache;
    private final ClientAuthorizationProvider authorizationProvider;
    private final ClientShareableService clientShareableService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final HttpServletRequest servletRequest;
    private final ClientRequestContextService clientRequestContextService;

    @Autowired
    public ClientPersonShareableV2EndpointsImpl(PersonService personService,
        ProgramDomainCache programCache,
        ClientAuthorizationProvider authorizationProvider,
        ClientShareableService clientShareableService,
        ConsumerEventSenderService consumerEventSenderService,
        @Context HttpServletRequest servletRequest,
        ClientRequestContextService clientRequestContextService) {
        this.personService = personService;
        this.programCache = programCache;
        this.authorizationProvider = authorizationProvider;
        this.clientShareableService = clientShareableService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.servletRequest = servletRequest;
        this.clientRequestContextService = clientRequestContextService;
    }

    @Override
    public ShareableV2Response create(String accessToken, String personId, CreateShareableV2Request request)
        throws UserAuthorizationRestException, PersonRestException, ClientShareableValidationV2RestException,
        ClientShareableCreateV2RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        PublicProgram program = getProgram(authorization);
        try {
            Person person = personService.getPerson(authorization, Id.valueOf(personId));
            ClientRequestContext requestContext =
                clientRequestContextService.createBuilder(authorization, servletRequest)
                    .withEventName(ConsumerEventName.EXTOLE_SHAREABLE.getEventName())
                    .withEventProcessing(configurator -> {
                        if (request.getData() != null) {
                            request.getData().forEach((k, v) -> configurator
                                .addData(new EventData(k, v, EventData.Source.REQUEST_BODY, false, true)));
                        }
                    })
                    .build();

            ClientShareable clientShareable = consumerEventSenderService
                .createInputEvent(authorization, requestContext.getProcessedRawEvent(), person.getId())
                .withLockDescription(new LockDescription("client-person-shareable-v2-endpoint-create"))
                .executeAndSend((personBuilder, originalPerson, inputEventBuilder) -> {
                    try {
                        ClientShareableBuilder builder =
                            clientShareableService.create(authorization.getClientId(), program.getId(), personBuilder);
                        if (authorization.isAuthorized(authorization.getClientId(),
                            Authorization.Scope.USER_SUPPORT)) {
                            builder.withIgnoringNaughtyWords();
                        }
                        if (!Strings.isNullOrEmpty(request.getLabel())) {
                            builder.withLabel(request.getLabel());
                        }
                        builder.withTargetUrl(request.getTargetUrl())
                            .withKey(request.getKey())
                            .withCode(request.getCode());

                        addContent(builder, request.getContent());
                        addData(builder, request.getData());

                        inputEventBuilder.withClientDomainContext(
                            new ClientDomainContext(program.getProgramDomain().toString(), program.getId()));

                        ClientShareable createdShareable = builder.save();
                        return new InputEventLockClosureResult<>(originalPerson, createdShareable);
                    } catch (ClientShareableValidationV2RestException | ShareableLabelNameLengthException
                        | ShareableLabelIllegalCharacterInNameException | ShareableTargetUrlTooLongException
                        | MalformedTargetUrlException | ShareableFieldLengthException | ShareableFieldValueException
                        | ShareableCodeTakenException | ShareableKeyTakenException | ShareableBlockedUrlException
                        | ShareableCodeTakenByPromotionException | ShareableCodeReservedException
                        | ShareableProgramNotFoundException | ShareableMissingProgramIdException e) {
                        throw new LockClosureException(e);
                    }
                }).getPreEventSendingResult();

            personService.updatePerson(authorization, Id.valueOf(personId),
                new LockDescription("client-person-shareable-v2-endpoint-update-person"),
                (personBuilder, initialPerson) -> {
                    try {
                        updateShareParameters(personBuilder, clientShareable.getId().getValue(),
                            request.getConsumerEvent());
                        return personBuilder.save();
                    } catch (FatalRestRuntimeException e) {
                        throw new LockClosureException(e);
                    }
                }, createConsumerEventSender(program));
            return toRestShareable(clientShareable);
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(ShareableCodeTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateV2RestException.class)
                    .withErrorCode(ClientShareableCreateV2RestException.CODE_TAKEN)
                    .addParameter("shareable_id", ((ShareableCodeTakenException) cause).getShareableId().orElse(null))
                    .addParameter("code_suggestions", ((ShareableCodeTakenException) cause).getSuggestions())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeReservedException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.SHAREABLE_CODE_CONTAINS_RESERVED_WORD)
                    .addParameter("reserved_word",
                        ((ShareableCodeReservedException) cause).getReservedWord())
                    .addParameter("code", ((ShareableCodeReservedException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.KEY_TAKEN)
                    .addParameter("shareable_id", ((ShareableKeyTakenException) cause).getShareableId())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(PersonNotRewardableException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateV2RestException.class)
                    .withErrorCode(ClientShareableCreateV2RestException.PERSON_NOT_REWARDABLE)
                    .addParameter("person_id", ((PersonNotRewardableException) cause).getPersonId())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableProgramNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateV2RestException.class)
                    .withErrorCode(ClientShareableCreateV2RestException.INVALID_PROGRAM_ID)
                    .addParameter("program_id", program.getId())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass()
                .isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label", ((ShareableLabelIllegalCharacterInNameException) cause).getLabelName())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableTargetUrlTooLongException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.TARGET_URL_LENGTH_OUT_OF_RANGE)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(MalformedTargetUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.TARGET_URL_INVALID)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableBlockedUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.TARGET_URL_INVALID)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateV2RestException.class)
                    .withErrorCode(ClientShareableCreateV2RestException.CODE_LENGTH_OUT_OF_RANGE)
                    .addParameter("code", request.getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldValueException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateV2RestException.class)
                    .withErrorCode(ClientShareableCreateV2RestException.CODE_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("code", request.getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenByPromotionException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableCreateV2RestException.class)
                    .withErrorCode(ClientShareableCreateV2RestException.CODE_TAKEN_BY_PROMOTION)
                    .addParameter("code", request.getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(FatalRestRuntimeException.class)) {
                throw (FatalRestRuntimeException) cause;
            } else if (cause.getClass().isAssignableFrom(ClientShareableValidationV2RestException.class)) {
                throw (ClientShareableValidationV2RestException) cause;
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (EventProcessorException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public ShareableV2Response get(String accessToken, String personId, String shareableId)
        throws ClientShareableV2RestException, PersonRestException, UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            personService.getPerson(authorization, Id.valueOf(personId));
            return toRestShareable(clientShareableService.get(authorization, Id.valueOf(shareableId)));
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableV2RestException.class)
                .withErrorCode(ClientShareableV2RestException.NOT_FOUND)
                .addParameter("shareable_id", shareableId)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public List<ShareableV2Response> getAll(String accessToken, String personId)
        throws UserAuthorizationRestException, PersonRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return clientShareableService.getAll(authorization, Id.valueOf(personId)).stream()
                .map(this::toRestShareable).collect(Collectors.toList());
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(PersonRestException.class)
                .withErrorCode(PersonRestException.PERSON_NOT_FOUND)
                .addParameter("person_id", personId)
                .withCause(e).build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        }
    }

    @Override
    public ShareableV2Response update(String accessToken, String personId, String shareableId,
        UpdateShareableV2Request request)
        throws UserAuthorizationRestException, PersonRestException, ClientShareableV2RestException,
        ClientShareableValidationV2RestException, ClientShareableCreateV2RestException {
        ClientAuthorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            personService.getPerson(authorization, Id.valueOf(personId)); // verify person access
            ClientShareable clientShareable =
                clientShareableService.get(authorization, Id.valueOf(shareableId));
            if (!personService.isSamePerson(authorization.getClientId(), Id.valueOf(personId),
                clientShareable.getPersonId())) {
                throw RestExceptionBuilder.newBuilder(ClientShareableV2RestException.class)
                    .withErrorCode(ClientShareableV2RestException.NOT_FOUND)
                    .addParameter("shareable_id", shareableId)
                    .build();
            }
            if (!personService.isAuthorized(authorization, clientShareable.getPersonId())) {
                throw new AuthorizationException(
                    "Not authorized to edit shareable with code " + clientShareable.getCode()
                        + " for person " + clientShareable.getPersonId()
                        + " with token " + HashAlgorithm.SHA1.hashString(authorization.getAccessToken()));
            }

            ClientRequestContext requestContext =
                clientRequestContextService.createBuilder(authorization, servletRequest)
                    .withEventName(ConsumerEventName.EXTOLE_SHAREABLE.getEventName())
                    .withEventProcessing(configurator -> {
                        if (request.getData() != null) {
                            request.getData().forEach((k, v) -> configurator
                                .addData(new EventData(k, v, EventData.Source.REQUEST_BODY, false, true)));
                        }
                    })
                    .build();

            PublicProgram program = getProgram(authorization, clientShareable.getProgramId());

            ClientShareable shareable = consumerEventSenderService
                .createInputEvent(authorization, requestContext.getProcessedRawEvent(),
                    clientShareable.getPersonId())
                .withLockDescription(new LockDescription("client-person-shareable-v2-endpoint-update"))
                .executeAndSend((personBuilder, originalPerson, inputEventBuilder) -> {
                    try {
                        ClientShareableBuilder builder =
                            clientShareableService.edit(authorization.getClientId(), clientShareable, personBuilder);

                        copyUpdateRequest(builder, request);
                        updateShareParameters(personBuilder, shareableId,
                            request.getConsumerEvent());

                        inputEventBuilder.withClientDomainContext(
                            new ClientDomainContext(program.getProgramDomain().toString(), program.getId()));

                        ClientShareable updatedShareable = builder.save();
                        return new InputEventLockClosureResult<>(originalPerson, updatedShareable);
                    } catch (ShareableNotFoundException | ClientShareableValidationV2RestException
                        | FatalRestRuntimeException | ShareableKeyTakenException
                        | ShareableMissingProgramIdException e) {
                        throw new LockClosureException(e);
                    }
                }).getPreEventSendingResult();
            return toRestShareable(shareable);
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.KEY_TAKEN)
                    .addParameter("shareable_id", ((ShareableKeyTakenException) cause).getShareableId())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(ClientShareableV2RestException.class)
                    .withErrorCode(ClientShareableV2RestException.NOT_FOUND)
                    .addParameter("shareable_id", shareableId)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ClientShareableValidationV2RestException.class)) {
                throw (ClientShareableValidationV2RestException) cause;
            } else if (cause.getClass().isAssignableFrom(FatalRestRuntimeException.class)) {
                throw (FatalRestRuntimeException) cause;
            } else if (cause.getClass().isAssignableFrom(AuthorizationException.class)) {
                throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                    .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableV2RestException.class)
                .withErrorCode(ClientShareableV2RestException.NOT_FOUND)
                .addParameter("shareable_id", shareableId)
                .withCause(e).build();
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
                .withCause(e)
                .build();
        }
    }

    private ConsumerEventSender createConsumerEventSender(PublicProgram program) {
        return consumerEventSenderService.createConsumerEventSender()
            .withClientDomainContext(new ClientDomainContext(program.getProgramDomain().toString(), program.getId()));
    }

    private void updateShareParameters(PersonBuilder personBuilder, String shareableId,
        ConsumerEventV2Request consumerEvent) {
        if (consumerEvent != null && consumerEvent.getSource() != null) {
            String sourceParameter = String.format("extole.shareable.%s.source", shareableId);

            try {
                personBuilder.addOrReplaceData(sourceParameter).withScope(Scope.PUBLIC)
                    .withValue(consumerEvent.getSource());
            } catch (PersonDataInvalidValueException | PersonDataValueLengthException | PersonDataNameLengthException
                | PersonDataInvalidNameException | ReadOnlyPersonDataException e) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e).build();
            }
        }
    }

    private void copyUpdateRequest(ClientShareableBuilder builder, UpdateShareableV2Request request)
        throws ClientShareableValidationV2RestException {
        if (!Strings.isNullOrEmpty(request.getTargetUrl())) {
            try {
                builder.withTargetUrl(request.getTargetUrl());
            } catch (MalformedTargetUrlException | ShareableBlockedUrlException e) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.TARGET_URL_INVALID)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } catch (ShareableTargetUrlTooLongException e) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.TARGET_URL_LENGTH_OUT_OF_RANGE)
                    .addParameter("target_url", request.getTargetUrl()).build();
            }
        }
        if (request.getKey() != null) {
            builder.withKey(request.getKey());
        }
        try {
            if (request.getLabel() != null) {
                builder.withLabel(request.getLabel());
            }
        } catch (ShareableLabelIllegalCharacterInNameException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                .withErrorCode(ClientShareableValidationV2RestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                .addParameter("label", e.getLabelName())
                .withCause(e).build();
        } catch (ShareableLabelNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                .withErrorCode(ClientShareableValidationV2RestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", e.getLabelName())
                .addParameter("min_length", Integer.valueOf(e.getMinLength()))
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .withCause(e).build();
        }
        addContent(builder, request.getContent());
        addData(builder, request.getData());
    }

    private PublicProgram getProgram(Authorization authorization) {
        return programCache.getDefaultProgram(authorization.getClientId())
            .orElseThrow(() -> RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).build());
    }

    private PublicProgram getProgram(Authorization authorization, Id<ProgramHandle> programId)
        throws ClientShareableCreateV2RestException {
        try {
            return programCache.getById(programId, authorization.getClientId());
        } catch (ProgramNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableCreateV2RestException.class)
                .withErrorCode(ClientShareableCreateV2RestException.INVALID_PROGRAM_ID)
                .addParameter("program_id", programId.getValue())
                .withCause(e).build();
        }
    }

    @Nullable
    private URI parseUrl(String urlValue) {
        URI result = null;
        if (urlValue != null) {
            try {
                new URL(urlValue);
                result = new URI(urlValue);
            } catch (MalformedURLException | URISyntaxException ignored) {
                // ignore this exception, will return null
            }
        }
        return result;
    }

    private void addContent(ClientShareableBuilder builder, ShareableV2Content content)
        throws ClientShareableValidationV2RestException {
        if (content == null) {
            return;
        }

        if (!Strings.isNullOrEmpty(content.getImageUrl())) {
            URI imageUrl = parseUrl(content.getImageUrl());
            if (imageUrl == null) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.IMAGE_URL_INVALID)
                    .addParameter("image_url", content.getImageUrl()).build();
            }
            builder.getContentBuilder().withImageUrl(imageUrl);
        }
        if (!Strings.isNullOrEmpty(content.getUrl())) {
            URI contentUrl = parseUrl(content.getUrl());
            if (contentUrl == null) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.CONTENT_URL_INVALID)
                    .addParameter("url", content.getUrl()).build();
            }
            try {
                builder.getContentBuilder().withUrl(contentUrl);
            } catch (ShareableBlockedUrlException e) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.CONTENT_URL_INVALID)
                    .addParameter("url", content.getUrl()).build();
            }
        }
        if (!Strings.isNullOrEmpty(content.getDescription())) {
            try {
                builder.getContentBuilder().withDescription(content.getDescription());
            } catch (ShareableContentDescriptionTooLongException e) {
                throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                    .withErrorCode(ClientShareableValidationV2RestException.CONTENT_DESCRIPTION_LENGTH_EXCEEDED)
                    .addParameter("description", content.getDescription()).build();
            }
        }
        if (!Strings.isNullOrEmpty(content.getTitle())) {
            builder.getContentBuilder().withTitle(content.getTitle());
        }
        if (!Strings.isNullOrEmpty(content.getPartnerContentId())) {
            builder.getContentBuilder().withPartnerContentId(content.getPartnerContentId());
        }
    }

    private void addData(ClientShareableBuilder builder, Map<String, String> data)
        throws ClientShareableValidationV2RestException {
        if (data.isEmpty()) {
            return;
        }
        builder.removeExistingData();
        for (Entry<String, String> dataEntry : data.entrySet()) {
            addData(builder, dataEntry.getKey(), dataEntry.getValue());
        }
    }

    private void addData(ClientShareableBuilder builder, String dataAttributeName, String dataAttributeValue)
        throws ClientShareableValidationV2RestException {
        try {
            builder.addData(dataAttributeName, dataAttributeValue);
        } catch (ShareableDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                .withErrorCode(ClientShareableValidationV2RestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (ShareableDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                .withErrorCode(ClientShareableValidationV2RestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (ShareableDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                .withErrorCode(ClientShareableValidationV2RestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (ShareableDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(ClientShareableValidationV2RestException.class)
                .withErrorCode(ClientShareableValidationV2RestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        }
    }

    static ShareableV2Content
        toRestShareableShareableContent(
            Optional<? extends ShareableContent> shareableContentOptional) {

        String partnerContentId = null;
        String title = null;
        String imageUrl = null;
        String description = null;
        String url = null;

        if (shareableContentOptional.isPresent()) {
            com.extole.person.service.shareable.ShareableContent shareableContent = shareableContentOptional.get();
            partnerContentId = shareableContent.getPartnerContentId().orElse(null);
            title = shareableContent.getTitle().orElse(null);
            description = shareableContent.getDescription().orElse(null);
            if (shareableContent.getImageUrl().isPresent()) {
                imageUrl = shareableContent.getImageUrl().get().toString();
            }
            if (shareableContent.getUrl().isPresent()) {
                url = shareableContent.getUrl().get().toString();
            }
        }

        return new ShareableV2Content(partnerContentId, title, imageUrl, description, url);
    }

    private ShareableV2Response toRestShareable(ClientShareable shareable) {
        return new ShareableV2Response(shareable.getId().getValue(),
            shareable.getKey(),
            shareable.getCode(),
            shareable.getLink().toString(),
            toRestShareableShareableContent(shareable.getContent()),
            shareable.getData(),
            shareable.getLabel().orElse(null));
    }

}
