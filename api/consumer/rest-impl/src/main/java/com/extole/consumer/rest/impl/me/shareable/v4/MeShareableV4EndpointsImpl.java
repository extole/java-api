package com.extole.consumer.rest.impl.me.shareable.v4;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.extole.consumer.event.service.ConsumerEventSenderService;
import com.extole.consumer.event.service.input.InputEventLockClosureResult;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.event.service.processor.ProcessedRawEvent;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.PollingStatus;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.shareable.v4.CreateMeShareableV4Request;
import com.extole.consumer.rest.me.shareable.v4.CreateMeShareableV4Response;
import com.extole.consumer.rest.me.shareable.v4.EditMeShareableV4Request;
import com.extole.consumer.rest.me.shareable.v4.MeShareableV4Endpoints;
import com.extole.consumer.rest.me.shareable.v4.MeShareableV4Response;
import com.extole.consumer.rest.me.shareable.v4.MeShareableV4RestException;
import com.extole.consumer.rest.shareable.v4.CreateShareableV4RestException;
import com.extole.consumer.rest.shareable.v4.ShareableCreateV4PollingResponse;
import com.extole.consumer.rest.shareable.v4.ShareableV4Content;
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
import com.extole.id.Id;
import com.extole.model.entity.program.PublicProgram;
import com.extole.person.service.profile.Person;
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
import com.extole.person.service.shareable.ShareableTargetUrlTooLongException;
import com.extole.security.backend.BackendAuthorizationProvider;

@Deprecated // TODO remove ENG-10127
@Provider
public class MeShareableV4EndpointsImpl implements MeShareableV4Endpoints {
    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerShareableService consumerShareableService;
    private final PersonService personService;
    private final ConsumerEventSenderService consumerEventSenderService;
    private final BackendAuthorizationProvider backendAuthorizationProvider;

    @Inject
    public MeShareableV4EndpointsImpl(@Context HttpServletRequest servletRequest,
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
    public List<MeShareableV4Response> getMeShareables(String accessToken) throws AuthorizationRestException {
        List<MeShareableV4Response> responses = new ArrayList<>();
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        List<ConsumerShareable> shareables = consumerShareableService.get(authorization);
        for (ConsumerShareable shareable : shareables) {
            responses.add(toMeShareableResponse(shareable));
        }
        return responses;
    }

    @Override
    public MeShareableV4Response getMeShareable(String accessToken, String shareableId,
        String promotionSource) throws AuthorizationRestException, MeShareableV4RestException {
        ConsumerRequestContext requestContext = consumerRequestContextService.createBuilder(servletRequest)
            .withEventName(ConsumerEventName.EXTOLE_SHAREABLE.getEventName())
            .withAccessToken(accessToken)
            .build();
        PersonAuthorization authorization = requestContext.getAuthorization();
        try {
            ConsumerShareable shareable = consumerShareableService.get(authorization, Id.valueOf(shareableId));
            if (promotionSource != null) {
                String sourceParameter = String.format("extole.shareable.%s.source", shareableId);
                try {
                    consumerEventSenderService
                        .createInputEvent(authorization, requestContext.getProcessedRawEvent())
                        .withLockDescription(new LockDescription("me-shareable-v4-endpoints-get-shareable"))
                        .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                            try {
                                personBuilder.addOrReplaceData(sourceParameter).withScope(Scope.PUBLIC)
                                    .withValue(promotionSource);
                            } catch (PersonDataInvalidValueException | PersonDataValueLengthException
                                | PersonDataInvalidNameException | PersonDataNameLengthException
                                | ReadOnlyPersonDataException e) {
                                throw new LockClosureException(e);
                            }
                            Person updatedPerson = personBuilder.save();
                            return new InputEventLockClosureResult<>(updatedPerson);
                        });
                } catch (LockClosureException | AuthorizationException | PersonNotFoundException e) {
                    throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                        .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e.getCause()).build();
                }
            }
            return toMeShareableResponse(shareable);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(MeShareableV4RestException.class)
                .withErrorCode(MeShareableV4RestException.INVALID_SHAREABLE_ID).withCause(e)
                .addParameter("shareable_id", shareableId).build();
        }
    }

    private static MeShareableV4Response toMeShareableResponse(ConsumerShareable shareable) {
        return new MeShareableV4Response(shareable.getId().getValue(), shareable.getCode(),
            shareable.getLink().toString(), shareable.getKey(), toShareableContent(shareable.getContent()),
            shareable.getData(), shareable.getLabel().orElse(null));
    }

    private static ShareableV4Content toShareableContent(Optional<ShareableContent> content) {
        String partnerContentId = null;
        String title = null;
        String imageUrl = null;
        String description = null;
        String url = null;
        if (content.isPresent()) {
            ShareableContent shareableContent = content.get();
            partnerContentId = shareableContent.getPartnerContentId().orElse(null);
            title = shareableContent.getTitle().orElse(null);
            imageUrl = shareableContent.getImageUrl().map(URI::toString).orElse(null);
            description = shareableContent.getDescription().orElse(null);
            url = shareableContent.getUrl().map(URI::toString).orElse(null);
        }
        return new ShareableV4Content(partnerContentId, title, imageUrl, description, url);
    }

    @Override
    public CreateMeShareableV4Response create(String accessToken, CreateMeShareableV4Request request)
        throws CreateShareableV4RestException, AuthorizationRestException {
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
        PublicProgram programDomain = processedRawEvent.getClientDomain();
        try {
            DeprecatedConsumerShareable deprecatedConsumerShareable =
                consumerEventSenderService
                    .createInputEvent(authorization, processedRawEvent)
                    .withLockDescription(new LockDescription("me-shareable-v4-endoint-create"))
                    .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                        try {
                            ConsumerShareableV4Builder builder =
                                consumerShareableService.createV4(authorization, programDomain, personBuilder);
                            if (!Strings.isNullOrEmpty(request.getCode())) {
                                builder.withCode(request.getCode());
                            }
                            builder.withKey(request.getKey());
                            builder.withTargetUrl(request.getTargetUrl());
                            if (!Strings.isNullOrEmpty(request.getLabel())) {
                                builder.withLabel(request.getLabel());
                            }

                            addContent(builder, request.getContent());
                            addData(builder, request.getData());

                            DeprecatedConsumerShareable result = builder.send();
                            return new InputEventLockClosureResult<>(result.getUpdatedPerson(), result);
                        } catch (ShareableCodeBlacklistedException | MalformedTargetUrlException
                            | ShareableFieldLengthException | ShareableFieldValueException | ShareableCodeTakenException
                            | ShareableKeyTakenException | ShareableLabelIllegalCharacterInNameException
                            | ShareableLabelNameLengthException | ShareableTargetUrlTooLongException
                            | CreateShareableV4RestException | PersonNotRewardableException
                            | ShareableBlockedUrlException | ShareableContentDescriptionTooLongException
                            | ShareableCodeTakenByPromotionException | ShareableCodeReservedException
                            | ShareableMissingProgramIdException e) {
                            throw new LockClosureException(e);
                        }
                    }).getPreEventSendingResult();
            return new CreateMeShareableV4Response(deprecatedConsumerShareable.getPollingId().getValue());
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(ShareableCodeBlacklistedException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.CODE_CONTAINS_PROFANE_WORD)
                    .addParameter("code", request.getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeReservedException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.SHAREABLE_CODE_CONTAINS_RESERVED_WORD)
                    .addParameter("reserved_word",
                        ((ShareableCodeReservedException) cause).getReservedWord())
                    .addParameter("code", ((ShareableCodeReservedException) cause).getCode())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(MalformedTargetUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.INVALID_TARGET_URL).withCause(cause)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableBlockedUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.INVALID_TARGET_URL).withCause(cause)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldLengthException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.CODE_LENGTH_OUT_OF_RANGE)
                    .addParameter("code", request.getCode()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableFieldValueException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.CODE_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("code", request.getCode()).withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.EXISTING_LINK)
                    .addParameter("link",
                        programDomain.getShareUri() + "/" + ((ShareableCodeTakenException) cause).getCode())
                    .addParameter("code_suggestions", ((ShareableCodeTakenException) cause).getSuggestions())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.KEY_TAKEN)
                    .addParameter("key", request.getKey())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label", ((ShareableLabelIllegalCharacterInNameException) cause).getLabelName())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass().isAssignableFrom(ShareableTargetUrlTooLongException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.TARGET_URL_LENGTH_OUT_OF_RANGE)
                    .addParameter("target_url", request.getTargetUrl())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(CreateShareableV4RestException.class)) {
                throw (CreateShareableV4RestException) cause;
            } else if (cause.getClass().isAssignableFrom(PersonNotRewardableException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.UNREWARDABLE)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableContentDescriptionTooLongException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.CONTENT_DESCRIPTION_LENGTH_EXCEEDED)
                    .addParameter("description", request.getContent().getDescription())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenByPromotionException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.CODE_TAKEN_BY_PROMOTION)
                    .addParameter("code", request.getCode())
                    .withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (AuthorizationException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CreateMeShareableV4Response edit(String accessToken, String shareableId, EditMeShareableV4Request request)
        throws CreateShareableV4RestException, AuthorizationRestException, MeShareableV4RestException {
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
            ConsumerShareable existingShareable = consumerShareableService.get(authorization, Id.valueOf(shareableId));

            if (!personService.isSamePerson(authorization.getClientId(),
                authorization.getIdentityId(), existingShareable.getPersonId())
                || !personService.isAuthorized(authorization, existingShareable.getPersonId())) {
                throw new AuthorizationException(
                    "Not authorized to edit shareable with code " + existingShareable.getCode() + " for person "
                        + existingShareable.getPersonId() + " with token " + authorization.getAccessToken());
            }
            if (!authorization.getIdentityId().equals(existingShareable.getPersonId()) &&
                !authorization.getScopes().contains(Authorization.Scope.VERIFIED_CONSUMER)) {
                throw new AuthorizationException(
                    "Not authorized to edit shareable with code " + existingShareable.getCode() + " for person "
                        + existingShareable.getPersonId() + " with token " + authorization.getAccessToken());
            }

            InputConsumerEvent inputEvent = consumerEventSenderService
                .createInputEvent(authorization, requestContext.getProcessedRawEvent())
                .send();

            Authorization backendAuthorization =
                backendAuthorizationProvider.getAuthorizationForBackend(authorization.getClientId());

            // ENG-19642 person update is performed using backendAuthorization hence cannot send it from input event
            DeprecatedConsumerShareable shareable =
                personService.updatePerson(backendAuthorization, existingShareable.getPersonId(),
                    new LockDescription("me-shareable-v4-endpoint-update"),
                    (personBuilder, initialPerson) -> {
                        try {
                            ConsumerShareableV4Builder builder =
                                consumerShareableService.editV4(existingShareable, personBuilder);

                            builder.withTargetUrl(request.getTargetUrl());
                            if (request.getKey() != null) {
                                builder.withKey(request.getKey());
                            }
                            if (!Strings.isNullOrEmpty(request.getLabel())) {
                                builder.withLabel(request.getLabel());
                            }
                            addContent(builder, request.getContent());
                            addData(builder, request.getData());
                            return builder.send();
                        } catch (ShareableNotFoundException | MalformedTargetUrlException
                            | ShareableKeyTakenException | ShareableLabelIllegalCharacterInNameException
                            | ShareableLabelNameLengthException | ShareableTargetUrlTooLongException
                            | CreateShareableV4RestException | AuthorizationException
                            | ShareableBlockedUrlException | ShareableContentDescriptionTooLongException
                            | ShareableMissingProgramIdException e) {
                            throw new LockClosureException(e);
                        }
                    }, consumerEventSenderService.createConsumerEventSender(inputEvent));

            return new CreateMeShareableV4Response(shareable.getPollingId().getValue());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                .withErrorCode(CreateShareableV4RestException.PERSON_NOT_FOUND)
                .addParameter("person_id", e.getPersonId())
                .withCause(e).build();
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(MalformedTargetUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.INVALID_TARGET_URL).withCause(cause)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableBlockedUrlException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.INVALID_TARGET_URL).withCause(cause)
                    .addParameter("target_url", request.getTargetUrl()).build();
            } else if (cause.getClass().isAssignableFrom(ShareableKeyTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.KEY_TAKEN)
                    .addParameter("key", request.getKey())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableCodeTakenException.class)) {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label", ((ShareableLabelIllegalCharacterInNameException) cause).getLabelName())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            } else if (cause.getClass().isAssignableFrom(ShareableTargetUrlTooLongException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.TARGET_URL_LENGTH_OUT_OF_RANGE)
                    .addParameter("target_url", request.getTargetUrl())
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(ShareableNotFoundException.class)) {
                throw RestExceptionBuilder.newBuilder(MeShareableV4RestException.class)
                    .withErrorCode(MeShareableV4RestException.INVALID_SHAREABLE_ID)
                    .addParameter("shareable_id", shareableId)
                    .withCause(cause).build();
            } else if (cause.getClass().isAssignableFrom(AuthorizationException.class)) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                    .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                    .build();
            } else if (cause.getClass().isAssignableFrom(ShareableContentDescriptionTooLongException.class)) {
                throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                    .withErrorCode(CreateShareableV4RestException.CONTENT_DESCRIPTION_LENGTH_EXCEEDED)
                    .addParameter("description", request.getContent().getDescription())
                    .withCause(cause).build();
            } else {
                throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                    .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
            }
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(MeShareableV4RestException.class)
                .withErrorCode(MeShareableV4RestException.INVALID_SHAREABLE_ID)
                .addParameter("shareable_id", shareableId)
                .withCause(e).build();
        }
    }

    private static void addContent(ConsumerShareableV4Builder builder, ShareableV4Content content)
        throws CreateShareableV4RestException, ShareableContentDescriptionTooLongException {
        if (content == null) {
            return;
        }
        try {
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
        } catch (MalformedImageUrlException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                .withErrorCode(CreateShareableV4RestException.INVALID_IMAGE_URL)
                .addParameter("image_url", content.getImageUrl())
                .withCause(e).build();
        } catch (MalformedPartnerContentUrlException | ShareableBlockedUrlException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                .withErrorCode(CreateShareableV4RestException.INVALID_PARTNER_CONTENT_URL)
                .addParameter("partner_content_url", content.getUrl())
                .withCause(e).build();
        }
    }

    private static void addData(ConsumerShareableV4Builder builder, Map<String, String> data)
        throws CreateShareableV4RestException {
        if (data.isEmpty()) {
            return;
        }
        builder.removeExistingData();
        for (Entry<String, String> dataEntry : data.entrySet()) {
            addData(builder, dataEntry.getKey(), dataEntry.getValue());
        }
    }

    private static void addData(ConsumerShareableV4Builder builder, String dataAttributeName, String dataAttributeValue)
        throws CreateShareableV4RestException {
        try {
            builder.addData(dataAttributeName, dataAttributeValue);
        } catch (ShareableDataAttributeNameInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                .withErrorCode(CreateShareableV4RestException.DATA_ATTRIBUTE_NAME_INVALID)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (ShareableDataAttributeValueInvalidException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                .withErrorCode(CreateShareableV4RestException.DATA_ATTRIBUTE_VALUE_INVALID)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (ShareableDataAttributeNameLengthException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                .withErrorCode(CreateShareableV4RestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        } catch (ShareableDataAttributeValueLengthException e) {
            throw RestExceptionBuilder.newBuilder(CreateShareableV4RestException.class)
                .withErrorCode(CreateShareableV4RestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                .addParameter("name", dataAttributeName)
                .withCause(e).build();
        }
    }

    @Override
    public ShareableCreateV4PollingResponse getStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        CreateShareableOperation operation = consumerShareableService.getStatus(authorization,
            Id.valueOf(pollingId));
        return new ShareableCreateV4PollingResponse(
            operation.getPollingId().getValue(),
            Optional.ofNullable(operation.getShareableId()).map(Id::getValue).orElse(null),
            PollingStatus.valueOf(operation.getStatus().name()),
            null);
    }
}
