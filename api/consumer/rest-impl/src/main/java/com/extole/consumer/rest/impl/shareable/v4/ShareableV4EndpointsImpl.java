package com.extole.consumer.rest.impl.shareable.v4;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.security.HashAlgorithm;
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.PollingStatus;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.shareable.v4.CreateShareableV4Request;
import com.extole.consumer.rest.shareable.v4.CreateShareableV4Response;
import com.extole.consumer.rest.shareable.v4.CreateShareableV4RestException;
import com.extole.consumer.rest.shareable.v4.GetShareableV4Response;
import com.extole.consumer.rest.shareable.v4.GetShareableV4RestException;
import com.extole.consumer.rest.shareable.v4.ShareableCreateV4PollingResponse;
import com.extole.consumer.rest.shareable.v4.ShareableV4Content;
import com.extole.consumer.rest.shareable.v4.ShareableV4Endpoints;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.shareable.ConsumerShareable;
import com.extole.consumer.service.shareable.ConsumerShareableService;
import com.extole.consumer.service.shareable.ConsumerShareableV4Builder;
import com.extole.consumer.service.shareable.CreateShareableOperation;
import com.extole.consumer.service.shareable.DeprecatedConsumerShareable;
import com.extole.consumer.service.shareable.MalformedImageUrlException;
import com.extole.consumer.service.shareable.MalformedPartnerContentUrlException;
import com.extole.consumer.service.shareable.MalformedTargetUrlException;
import com.extole.consumer.service.shareable.ShareableCodeBlacklistedException;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.event.consumer.input.InputConsumerEvent;
import com.extole.id.DeprecatedIdGenerator;
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.shareable.PersonNotRewardableException;
import com.extole.person.service.shareable.ShareableBlockedUrlException;
import com.extole.person.service.shareable.ShareableCodeReservedException;
import com.extole.person.service.shareable.ShareableCodeTakenByPromotionException;
import com.extole.person.service.shareable.ShareableCodeTakenException;
import com.extole.person.service.shareable.ShareableContentDescriptionTooLongException;
import com.extole.person.service.shareable.ShareableDestinationUrlTooLongException;
import com.extole.person.service.shareable.ShareableFieldLengthException;
import com.extole.person.service.shareable.ShareableFieldValueException;
import com.extole.person.service.shareable.ShareableKeyTakenException;
import com.extole.person.service.shareable.ShareableLabelIllegalCharacterInNameException;
import com.extole.person.service.shareable.ShareableLabelNameLengthException;
import com.extole.person.service.shareable.ShareableMissingProgramIdException;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.security.backend.BackendAuthorizationProvider;

@Deprecated // TODO remove ENG-10127
@Provider
public class ShareableV4EndpointsImpl implements ShareableV4Endpoints {
    private final HttpServletRequest servletRequest;
    private final ConsumerShareableService consumerShareableService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final PersonService personService;
    private final DeprecatedIdGenerator<Long> idGenerator;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    @Inject
    public ShareableV4EndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerShareableService shareableService,
        ConsumerRequestContextService consumerRequestContextService,
        PersonService personService,
        DeprecatedIdGenerator<Long> idGenerator,
        ConsumerEventSenderService consumerEventSenderService,
        BackendAuthorizationProvider backendAuthorizationProvider) {
        this.consumerShareableService = shareableService;
        this.consumerRequestContextService = consumerRequestContextService;
        this.servletRequest = servletRequest;
        this.personService = personService;
        this.idGenerator = idGenerator;
        this.consumerEventSenderService = consumerEventSenderService;
        this.backendAuthorizationProvider = backendAuthorizationProvider;
    }

    @Deprecated // TODO remove ENG-10127
    @Override
    public CreateShareableV4Response create(String accessToken, CreateShareableV4Request request)
        throws CreateShareableV4RestException, AuthorizationRestException {
        ConsumerRequestContext context = consumerRequestContextService.createBuilder(servletRequest)
            .withEventName(ConsumerEventName.EXTOLE_SHAREABLE.getEventName())
            .withAccessToken(accessToken)
            .build();

        PersonAuthorization authorization = context.getAuthorization();
        ProcessedRawEvent processedRawEvent = context.getProcessedRawEvent();
        PublicProgram programDomain = processedRawEvent.getClientDomain();
        try {
            String key = request.getKey();
            if (key == null) {
                key = idGenerator.generateId().toString();
            }
            ConsumerShareable existingShareable = consumerShareableService.existingShareableWithKey(authorization, key);
            DeprecatedConsumerShareable shareable;
            if (existingShareable != null) {
                shareable = updateShareable(authorization, processedRawEvent, request, existingShareable, key);
            } else {
                shareable = createShareable(authorization, processedRawEvent, request, key);
            }
            // TODO look up share uri through shareable once cache invalidation frame is in place, ENG-1393
            return new CreateShareableV4Response(shareable.getPollingId().getValue(),
                shareable.getShareable().getShareableId().getValue(),
                shareable.getShareable().getCode(),
                programDomain.getShareUri().toString(),
                shareable.getShareable().getProgramId().getValue());
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(MalformedTargetUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.INVALID_TARGET_URL)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeReservedException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.SHAREABLE_CODE_CONTAINS_RESERVED_WORD)
                    .addParameter("reserved_word",
                        ((ShareableCodeReservedException) cause).getReservedWord())
                    .addParameter("code", ((ShareableCodeReservedException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableBlockedUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.INVALID_TARGET_URL).withCause(cause)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.EXISTING_LINK)
                    .addParameter("link",
                        programDomain.getShareUri() + "/" + ((ShareableCodeTakenException) cause).getCode())
                    .addParameter("code_suggestions", ((ShareableCodeTakenException) cause).getSuggestions()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.KEY_TAKEN)
                    .addParameter("key", request.getKey())
                    .build();
            } else if (cause.getClass().isAssignableFrom(MalformedImageUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.INVALID_IMAGE_URL)
                    .addParameter("image_url",
                        Optional.ofNullable(request.getContent()).map(ShareableV4Content::getImageUrl).orElse(null))
                    .build();
            } else if (cause.getClass().isAssignableFrom(MalformedPartnerContentUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.INVALID_PARTNER_CONTENT_URL)
                    .addParameter("partner_content_url",
                        Optional.ofNullable(request.getContent()).map(ShareableV4Content::getUrl).orElse(null))
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.CODE_LENGTH_OUT_OF_RANGE)
                    .addParameter("code", request.getCode())
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldValueException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.CODE_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("code", request.getCode())
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeBlacklistedException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.CODE_CONTAINS_PROFANE_WORD)
                    .addParameter("code", request.getCode())
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenByPromotionException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.CODE_TAKEN_BY_PROMOTION)
                    .addParameter("code", request.getCode())
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label", ((ShareableLabelIllegalCharacterInNameException) cause).getLabelName())
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass().isAssignableFrom(ShareableDestinationUrlTooLongException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.EXTOLE_DESTINATION_LENGTH_OUT_OF_RANGE)
                    .addParameter("target_url", request.getTargetUrl())
                    .build();
            } else if (cause.getClass().isAssignableFrom(PersonNotRewardableException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.UNREWARDABLE)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(AuthorizationException.class)) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class).withCause(cause)
                    .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                    .build();
            } else if (cause.getClass().isAssignableFrom(PersonNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.PERSON_NOT_FOUND)
                    .addParameter("person_id", ((PersonNotFoundException) cause).getPersonId())
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableContentDescriptionTooLongException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(cause)
                    .withErrorCode(CreateShareableV4RestException.CONTENT_DESCRIPTION_LENGTH_EXCEEDED)
                    .addParameter("description", request.getContent().getDescription())
                    .build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class).withCause(e)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class).withCause(e)
                .withErrorCode(CreateShareableV4RestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .build();
        }
    }

    private DeprecatedConsumerShareable updateShareable(PersonAuthorization authorization,
        ProcessedRawEvent processedRawEvent, CreateShareableV4Request request,
        ConsumerShareable existingShareableWithKey, String shareableKey)
        throws AuthorizationException, LockClosureException, PersonNotFoundException {

        if (!personService.isSamePerson(authorization.getClientId(),
            Id.valueOf(authorization.getIdentityId().getValue()), existingShareableWithKey.getPersonId())
            || !personService.isAuthorized(authorization, existingShareableWithKey.getPersonId())) {
            throw new AuthorizationException(
                "Not authorized to edit shareable with code " + existingShareableWithKey.getCode()
                    + " for person " + existingShareableWithKey.getPersonId()
                    + " with token " + HashAlgorithm.SHA1.hashString(authorization.getAccessToken()));
        }

        InputConsumerEvent inputEvent =
            consumerEventSenderService.createInputEvent(authorization, processedRawEvent, authorization.getIdentity())
                .send();

        Authorization backendAuthorization =
            backendAuthorizationProvider.getAuthorizationForBackend(authorization.getClientId());

        // ENG-19642 person update is performed using backendAuthorization hence cannot send it from input event
        return personService.updatePerson(backendAuthorization, existingShareableWithKey.getPersonId(),
            new LockDescription("shareable-v4-endpoint-update"),
            (personBuilder, originalPersonProfile) -> {
                try {
                    ConsumerShareableV4Builder builder =
                        consumerShareableService.editV4(existingShareableWithKey, personBuilder);

                    builder.withKey(shareableKey);
                    if (!Strings.isNullOrEmpty(request.getTargetUrl())) {
                        builder.withDestinationUrl(request.getTargetUrl());
                    }
                    if (!Strings.isNullOrEmpty(request.getCode())) {
                        builder.withCode(request.getCode());
                    }
                    if (!Strings.isNullOrEmpty(request.getLabel())) {
                        builder.withLabel(request.getLabel());
                    }
                    if (request.getContent() != null) {
                        builder.getContentBuilder()
                            .withPartnerContentId(request.getContent().getPartnerContentId())
                            .withTitle(request.getContent().getTitle())
                            .withImageUrl(request.getContent().getImageUrl())
                            .withDescription(request.getContent().getDescription())
                            .withUrl(request.getContent().getUrl());
                    }
                    return builder.send();
                } catch (ShareableNotFoundException
                    | MalformedTargetUrlException
                    | ShareableCodeTakenException | ShareableKeyTakenException
                    | MalformedImageUrlException
                    | MalformedPartnerContentUrlException | ShareableLabelIllegalCharacterInNameException
                    | ShareableLabelNameLengthException | ShareableDestinationUrlTooLongException
                    | ShareableCodeBlacklistedException | ShareableFieldLengthException
                    | ShareableFieldValueException | AuthorizationException
                    | ShareableBlockedUrlException | ShareableContentDescriptionTooLongException
                    | ShareableCodeTakenByPromotionException | ShareableCodeReservedException
                    | ShareableMissingProgramIdException e) {
                    throw new LockClosureException(e);
                }
            }, consumerEventSenderService.createConsumerEventSender(inputEvent));
    }

    private DeprecatedConsumerShareable createShareable(PersonAuthorization authorization,
        ProcessedRawEvent processedRawEvent, CreateShareableV4Request request, String shareableKey)
        throws LockClosureException, AuthorizationException {

        return consumerEventSenderService
            .createInputEvent(authorization, processedRawEvent, authorization.getIdentity())
            .withLockDescription(new LockDescription("shareable-v4-endpoint-create"))
            .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                try {
                    ConsumerShareableV4Builder builder = consumerShareableService.createV4(authorization,
                        processedRawEvent.getClientDomain(), personBuilder);
                    builder.withCode(request.getCode()).withKey(shareableKey);
                    if (!Strings.isNullOrEmpty(request.getTargetUrl())) {
                        builder.withDestinationUrl(request.getTargetUrl());
                    }
                    if (!Strings.isNullOrEmpty(request.getLabel())) {
                        builder.withLabel(request.getLabel());
                    }
                    if (request.getContent() != null) {
                        builder.getContentBuilder()
                            .withPartnerContentId(request.getContent().getPartnerContentId())
                            .withTitle(request.getContent().getTitle())
                            .withImageUrl(request.getContent().getImageUrl())
                            .withDescription(request.getContent().getDescription())
                            .withUrl(request.getContent().getUrl());
                    }
                    DeprecatedConsumerShareable deprecatedConsumerShareable = builder.send();
                    return new InputEventLockClosureResult<>(deprecatedConsumerShareable.getUpdatedPerson(),
                        deprecatedConsumerShareable);
                } catch (PersonNotRewardableException | MalformedTargetUrlException
                    | ShareableCodeTakenException | ShareableKeyTakenException
                    | MalformedImageUrlException
                    | MalformedPartnerContentUrlException | ShareableLabelIllegalCharacterInNameException
                    | ShareableLabelNameLengthException | ShareableDestinationUrlTooLongException
                    | ShareableCodeBlacklistedException | ShareableFieldLengthException
                    | ShareableFieldValueException | ShareableBlockedUrlException
                    | ShareableContentDescriptionTooLongException
                    | ShareableCodeTakenByPromotionException | ShareableCodeReservedException
                    | ShareableMissingProgramIdException e) {
                    throw new LockClosureException(e);
                }
            }).getPreEventSendingResult();
    }

    @Deprecated // TODO remove ENG-10127
    @Override
    public GetShareableV4Response get(String accessToken, String shareableId)
        throws AuthorizationRestException, GetShareableV4RestException {
        try {
            Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
                .withAccessToken(accessToken)
                .build()
                .getAuthorization();
            ConsumerShareable shareable = consumerShareableService.get(authorization, Id.valueOf(shareableId));
            return toGetShareableResponse(shareable);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(GetShareableV4RestException.class)
                .withErrorCode(GetShareableV4RestException.NOT_FOUND)
                .addParameter("shareable_id", shareableId)
                .withCause(e).build();
        }
    }

    @Override
    public List<GetShareableV4Response> getShareables(String accessToken, String code)
        throws AuthorizationRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        List<ConsumerShareable> shareables = new ArrayList<>();
        try {
            if (!Strings.isNullOrEmpty(code)) {
                shareables.add(consumerShareableService.getByCode(authorization, code));
            }
        } catch (ShareableNotFoundException ignored) {
            // ignored
        }

        List<GetShareableV4Response> responses = new ArrayList<>();
        for (ConsumerShareable shareable : shareables) {
            responses.add(toGetShareableResponse(shareable));
        }

        return responses;
    }

    private static GetShareableV4Response toGetShareableResponse(ConsumerShareable shareable) {
        return new GetShareableV4Response(shareable.getId().getValue(), shareable.getCode(),
            shareable.getLink().toString(), shareable.getKey(), toShareableContent(shareable.getContent()),
            shareable.getData(), shareable.getLabel().orElse(null), shareable.getPersonId().getValue());
    }

    private static ShareableV4Content
        toShareableContent(Optional<com.extole.person.service.shareable.ShareableContent> content) {
        String partnerContentId = null;
        String title = null;
        String imageUrl = null;
        String description = null;
        String url = null;
        if (content.isPresent()) {
            com.extole.person.service.shareable.ShareableContent shareableContent = content.get();
            partnerContentId = shareableContent.getPartnerContentId().orElse(null);
            title = shareableContent.getTitle().orElse(null);
            imageUrl = shareableContent.getImageUrl().map(URI::toString).orElse(null);
            description = shareableContent.getDescription().orElse(null);
            url = shareableContent.getUrl().map(URI::toString).orElse(null);
        }
        return new ShareableV4Content(partnerContentId, title, imageUrl, description, url);
    }

    @Deprecated // TODO remove ENG-10127
    @Override
    public ShareableCreateV4PollingResponse getStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        CreateShareableOperation operation = consumerShareableService.getStatus(authorization, Id.valueOf(pollingId));
        return new ShareableCreateV4PollingResponse(operation.getPollingId().getValue(),
            Optional.ofNullable(operation.getShareableId()).map(Id::getValue).orElse(null),
            PollingStatus.valueOf(operation.getStatus().name()),
            null);
    }
}
