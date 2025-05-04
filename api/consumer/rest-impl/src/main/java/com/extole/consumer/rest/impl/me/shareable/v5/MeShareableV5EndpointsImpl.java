package com.extole.consumer.rest.impl.me.shareable.v5;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestErrorBuilder;
import com.extole.common.security.HashAlgorithm;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.PollingStatus;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.shareable.v5.CreateMeShareableV5Error;
import com.extole.consumer.rest.me.shareable.v5.CreateMeShareableV5Request;
import com.extole.consumer.rest.me.shareable.v5.CreateMeShareableV5Response;
import com.extole.consumer.rest.me.shareable.v5.MeShareableV5Endpoints;
import com.extole.consumer.rest.me.shareable.v5.MeShareableV5Response;
import com.extole.consumer.rest.me.shareable.v5.ShareableV5Content;
import com.extole.consumer.rest.me.shareable.v5.ShareableV5PollingResponse;
import com.extole.consumer.rest.me.shareable.v5.ShareableV5RestException;
import com.extole.consumer.rest.me.shareable.v5.UpdateMeShareableV5Request;
import com.extole.consumer.rest.me.shareable.v5.UpdateMeShareableV5Response;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.shareable.ConsumerShareable;
import com.extole.consumer.service.shareable.ConsumerShareableService;
import com.extole.consumer.service.shareable.ConsumerShareableV5Builder;
import com.extole.consumer.service.shareable.ConsumerShareableV5Result;
import com.extole.consumer.service.shareable.CreateShareableOperation;
import com.extole.consumer.service.shareable.CreateShareableOperation.Error;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.event.consumer.input.InputConsumerEvent;
import com.extole.event.pending.operation.PendingOperationStatus;
import com.extole.id.Id;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.shareable.PersonNotRewardableException;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.security.backend.BackendAuthorizationProvider;

@Provider
public class MeShareableV5EndpointsImpl implements MeShareableV5Endpoints {
    private static final Logger LOG = LoggerFactory.getLogger(MeShareableV5EndpointsImpl.class);

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerShareableService consumerShareableService;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    @Inject
    public MeShareableV5EndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        ConsumerShareableService consumerShareableService,
        PersonService personService,
        ConsumerEventSenderService consumerEventSenderService,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.consumerShareableService = consumerShareableService;
        this.personService = personService;
        this.consumerEventSenderService = consumerEventSenderService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    @Override
    public List<MeShareableV5Response> getShareables(String accessToken, String key, String label)
        throws AuthorizationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        return consumerShareableService.get(authorization).stream()
            .filter(shareable -> filterShareable(shareable, key, label))
            .map(this::toMeShareableResponse)
            .collect(Collectors.toList());
    }

    private static boolean filterShareable(ConsumerShareable shareable, String key, String label) {
        boolean labelMatch;
        boolean keyMatch;

        if (!StringUtils.isEmpty(label)) {
            labelMatch = shareable.getLabel().isPresent() && label.equals(shareable.getLabel().get());
        } else {
            labelMatch = true;
        }
        if (!StringUtils.isEmpty(key)) {
            keyMatch = key.equalsIgnoreCase(shareable.getKey());
        } else {
            keyMatch = true;
        }
        return labelMatch && keyMatch;
    }

    @Override
    public MeShareableV5Response getShareable(String accessToken, String code)
        throws AuthorizationRestException, ShareableV5RestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            ConsumerShareable shareable = consumerShareableService.getByCode(authorization, code);
            return toMeShareableResponse(shareable);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareableV5RestException.class)
                .withErrorCode(ShareableV5RestException.SHAREABLE_NOT_FOUND).addParameter("code", code).withCause(e)
                .build();
        }
    }

    @Override
    public CreateMeShareableV5Response create(String accessToken, CreateMeShareableV5Request request)
        throws AuthorizationRestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withEventName(ConsumerEventName.EXTOLE_SHAREABLE.getEventName())
            .withEventProcessing(configurator -> {
                request.getData().forEach((key, value) -> {
                    configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                });
            })
            .build();

        PersonAuthorization authorization = requestContext.getAuthorization();
        ProcessedRawEvent processedRawEvent = requestContext.getProcessedRawEvent();

        try {
            Id<?> pollingId = consumerEventSenderService
                .createInputEvent(authorization, processedRawEvent, authorization.getIdentity())
                .withLockDescription(new LockDescription("me-shareable-endpoints-create"))
                .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                    ConsumerShareableV5Builder builder = consumerShareableService
                        .createV5(processedRawEvent.getClientDomain(), personBuilder)
                        .withCheckKeyUniqueness(true);
                    if (!Strings.isNullOrEmpty(request.getCode())) {
                        builder.withCode(request.getCode());
                    }
                    builder.withKey(request.getKey());
                    builder.withLabel(request.getLabel());

                    addContent(builder, request.getContent());
                    addData(builder, request.getData());

                    ConsumerShareableV5Result result = builder.send();
                    Person updatedPerson = result.getUpdatedPerson().orElse(person);
                    return new InputEventLockClosureResult<>(updatedPerson, result.getPollingId());
                }).getPreEventSendingResult();
            return new CreateMeShareableV5Response(pollingId.getValue());
        } catch (LockClosureException | AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e.getCause()).build();
        }
    }

    @Override
    public UpdateMeShareableV5Response update(String accessToken, String code, UpdateMeShareableV5Request request)
        throws AuthorizationRestException, ShareableV5RestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withEventName(ConsumerEventName.EXTOLE_SHAREABLE.getEventName())
            .withEventProcessing(configurator -> {
                request.getData().forEach((key, value) -> {
                    configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                });
            })
            .build();
        PersonAuthorization authorization = requestContext.getAuthorization();
        try {
            ConsumerShareable existingShareable = consumerShareableService.getByCode(authorization, code);
            if (!personService.isSamePerson(authorization.getClientId(),
                authorization.getIdentityId(), existingShareable.getPersonId())
                || !personService.isAuthorized(authorization, existingShareable.getPersonId())) {
                throw new AuthorizationException(
                    "Not authorized to edit shareable with code " + existingShareable.getCode()
                        + " for person " + existingShareable.getPersonId()
                        + " with token " + HashAlgorithm.SHA1.hashString(authorization.getAccessToken()));
            }

            InputConsumerEvent inputEvent = consumerEventSenderService
                .createInputEvent(authorization, requestContext.getProcessedRawEvent(), authorization.getIdentity())
                .send();

            Authorization backendAuthorization =
                backendAuthorizationProvider.getAuthorizationForBackend(authorization.getClientId());

            // ENG-19642 person update is performed using backendAuthorization hence cannot send it from input event
            Id<?> pollingId = personService.updatePerson(backendAuthorization, existingShareable.getPersonId(),
                new LockDescription("me-shareable-endpoint-update"),
                (personBuilder, originalPersonProfile) -> {
                    ConsumerShareableV5Builder builder =
                        consumerShareableService.editV5(existingShareable,
                            personBuilder);
                    if (request.getKey() != null) {
                        builder.withKey(request.getKey());
                    }
                    if (!Strings.isNullOrEmpty(request.getLabel())) {
                        builder.withLabel(request.getLabel());
                    }

                    addContent(builder, request.getContent());
                    addData(builder, request.getData());

                    return builder.send().getPollingId();
                }, consumerEventSenderService.createConsumerEventSender(inputEvent));
            return new UpdateMeShareableV5Response(pollingId.getValue());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareableV5RestException.class)
                .withErrorCode(ShareableV5RestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .build();
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(PersonNotRewardableException.class)) {
                throw RestExceptionBuilder.newBuilder(ShareableV5RestException.class)
                    .withErrorCode(ShareableV5RestException.PERSON_NOT_REWARDABLE)
                    .addParameter("person_id", authorization.getIdentityId().getValue())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(AuthorizationException.class)) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                    .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                    .withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareableV5RestException.class)
                .withErrorCode(ShareableV5RestException.SHAREABLE_NOT_FOUND)
                .addParameter("code", code)
                .withCause(e).build();
        }
    }

    private static void addContent(ConsumerShareableV5Builder builder, ShareableV5Content content) {
        if (content == null) {
            return;
        }
        if (!Strings.isNullOrEmpty(content.getPartnerContentId())) {
            builder.getContentBuilder().withPartnerContentId(content.getPartnerContentId());
        }
        if (!Strings.isNullOrEmpty(content.getTitle())) {
            builder.getContentBuilder().withTitle(content.getTitle());
        }
        if (!Strings.isNullOrEmpty(content.getDescription())) {
            builder.getContentBuilder().withDescription(content.getDescription());
        }
        if (!Strings.isNullOrEmpty(content.getImageUrl())) {
            builder.getContentBuilder().withImageUrl(content.getImageUrl());
        }
        if (!Strings.isNullOrEmpty(content.getUrl())) {
            builder.getContentBuilder().withUrl(content.getUrl());
        }
    }

    private static void addData(ConsumerShareableV5Builder builder, Map<String, String> data) {
        if (data.isEmpty()) {
            return;
        }
        builder.removeExistingData();
        for (Entry<String, String> dataEntry : data.entrySet()) {
            builder.addData(dataEntry.getKey(), dataEntry.getValue());
        }
    }

    @Override
    public ShareableV5PollingResponse getStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        CreateShareableOperation operation = consumerShareableService.getStatus(authorization, Id.valueOf(pollingId));

        MeShareableV5Response myShareableResponse = null;
        if (operation.getStatus() == PendingOperationStatus.SUCCEEDED) {
            try {
                ConsumerShareable customerShareable =
                    consumerShareableService.getByCode(authorization, operation.getCode());
                myShareableResponse = toMeShareableResponse(customerShareable);
            } catch (ShareableNotFoundException e) {
                LOG.error("Shareable not found for code: {} client_id: {}",
                    operation.getCode(),
                    authorization.getClientId(),
                    e);

                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(e)
                    .build();
            }
        }
        return new ShareableV5PollingResponse(operation.getPollingId().getValue(),
            PollingStatus.valueOf(operation.getStatus().name()), operation.getCode(), toErrorResponse(operation),
            myShareableResponse);
    }

    @Nullable
    private static CreateMeShareableV5Error toErrorResponse(CreateShareableOperation operation) {
        if (!operation.getStatus().isFailure()) {
            return null;
        }

        ErrorCode<CreateMeShareableV5Error> error = toErrorCode(operation.getError());
        if (error.getName().equalsIgnoreCase(CreateMeShareableV5Error.CODE_TAKEN.getName())) {
            return new CreateMeShareableV5Error(new RestErrorBuilder()
                .withUniqueId(operation.getPollingId().getValue())
                .withHttpStatusCode(error.getHttpCode())
                .withCode(error.getName())
                .withMessage(error.getMessage())
                .withParameters(ImmutableMap.of("code_suggestions", operation.getSuggestions()))
                .build());
        }
        return new CreateMeShareableV5Error(
            new RestErrorBuilder()
                .withUniqueId(operation.getPollingId().getValue())
                .withHttpStatusCode(error.getHttpCode())
                .withCode(error.getName())
                .withMessage(error.getMessage())
                .withParameters(null)
                .build());
    }

    private static ErrorCode<CreateMeShareableV5Error> toErrorCode(Error error) {
        switch (error) {
            case CODE_CONTAINS_RESERVED_WORD:
                return CreateMeShareableV5Error.SHAREABLE_CODE_CONTAINS_RESERVED_WORD;
            case CODE_CONTAINS_ILLEGAL_CHARACTER:
                return CreateMeShareableV5Error.CODE_CONTAINS_ILLEGAL_CHARACTER;
            case CODE_CONTAINS_PROFANE_WORD:
                return CreateMeShareableV5Error.CODE_CONTAINS_PROFANE_WORD;
            case CODE_LENGTH_OUT_OF_RANGE:
                return CreateMeShareableV5Error.CODE_LENGTH_OUT_OF_RANGE;
            case CODE_TAKEN:
                return CreateMeShareableV5Error.CODE_TAKEN;
            case CODE_TAKEN_BY_PROMOTION:
                return CreateMeShareableV5Error.CODE_TAKEN_BY_PROMOTION;
            case CONTENT_DESCRIPTION_LENGTH_EXCEEDED:
                return CreateMeShareableV5Error.CONTENT_DESCRIPTION_LENGTH_EXCEEDED;
            case CONTENT_IMAGE_URL_INVALID:
                return CreateMeShareableV5Error.CONTENT_IMAGE_URL_INVALID;
            case CONTENT_URL_INVALID:
                return CreateMeShareableV5Error.CONTENT_URL_INVALID;
            case DATA_ATTRIBUTE_NAME_INVALID:
                return CreateMeShareableV5Error.DATA_ATTRIBUTE_NAME_INVALID;
            case DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE:
                return CreateMeShareableV5Error.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE;
            case DATA_ATTRIBUTE_VALUE_INVALID:
                return CreateMeShareableV5Error.DATA_ATTRIBUTE_VALUE_INVALID;
            case DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE:
                return CreateMeShareableV5Error.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE;
            case KEY_TAKEN:
                return CreateMeShareableV5Error.KEY_TAKEN;
            case LABEL_IS_MISSING:
                return CreateMeShareableV5Error.LABEL_IS_MISSING;
            case LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER:
                return CreateMeShareableV5Error.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER;
            case LABEL_NAME_LENGTH_OUT_OF_RANGE:
                return CreateMeShareableV5Error.LABEL_NAME_LENGTH_OUT_OF_RANGE;
            default:
                return CreateMeShareableV5Error.SERVER_ERROR;
        }
    }

    private MeShareableV5Response toMeShareableResponse(ConsumerShareable shareable) {
        return new MeShareableV5Response(shareable.getCode(), shareable.getKey(), shareable.getLabel().orElse(null),
            shareable.getLink().toString(), toShareableContent(shareable.getContent()), shareable.getData());
    }

    @Nullable
    static ShareableV5Content
        toShareableContent(Optional<com.extole.person.service.shareable.ShareableContent> content) {
        if (!content.isPresent()) {
            return null;
        }
        com.extole.person.service.shareable.ShareableContent shareableContent = content.get();
        String partnerContentId = shareableContent.getPartnerContentId().orElse(null);
        String title = shareableContent.getTitle().orElse(null);
        String imageUrl = shareableContent.getImageUrl().map(URI::toString).orElse(null);
        String description = shareableContent.getDescription().orElse(null);
        String url = shareableContent.getUrl().map(URI::toString).orElse(null);
        return new ShareableV5Content(partnerContentId, title, imageUrl, description, url);
    }

}
