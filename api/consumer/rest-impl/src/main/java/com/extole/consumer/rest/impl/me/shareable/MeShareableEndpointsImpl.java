package com.extole.consumer.rest.impl.me.shareable;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import org.springframework.util.StringUtils;

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
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.me.shareable.CreateMeShareableRequest;
import com.extole.consumer.rest.me.shareable.CreateMeShareableRestException;
import com.extole.consumer.rest.me.shareable.MeShareableEndpoints;
import com.extole.consumer.rest.me.shareable.MeShareableResponse;
import com.extole.consumer.rest.me.shareable.ShareableContent;
import com.extole.consumer.rest.me.shareable.ShareableRestException;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.shareable.ConsumerShareable;
import com.extole.consumer.service.shareable.ConsumerShareableBuilder;
import com.extole.consumer.service.shareable.ConsumerShareableService;
import com.extole.consumer.service.shareable.MalformedImageUrlException;
import com.extole.consumer.service.shareable.MalformedPartnerContentUrlException;
import com.extole.consumer.service.shareable.ShareableCodeBlacklistedException;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.person.service.profile.Person;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.shareable.Shareable;
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
import com.extole.person.service.shareable.ShareableKeyTakenException;
import com.extole.person.service.shareable.ShareableLabelIllegalCharacterInNameException;
import com.extole.person.service.shareable.ShareableLabelNameLengthException;
import com.extole.person.service.shareable.ShareableMissingProgramIdException;
import com.extole.person.service.shareable.ShareableNotFoundException;

@Provider
public class MeShareableEndpointsImpl implements MeShareableEndpoints {
    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final ConsumerShareableService consumerShareableService;
    private final ConsumerEventSenderService consumerEventSenderService;

    @Inject
    public MeShareableEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        ConsumerShareableService consumerShareableService,
        ConsumerEventSenderService consumerEventSenderService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.consumerShareableService = consumerShareableService;
        this.consumerEventSenderService = consumerEventSenderService;
    }

    @Override
    public List<MeShareableResponse> getShareables(String accessToken, String key, String label)
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

    @Override
    public MeShareableResponse getShareable(String accessToken, String code)
        throws AuthorizationRestException, ShareableRestException {
        Authorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        try {
            ConsumerShareable shareable = consumerShareableService.getByCode(authorization, code);
            return toMeShareableResponse(shareable);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(ShareableRestException.class)
                .withErrorCode(ShareableRestException.SHAREABLE_NOT_FOUND).addParameter("code", code).withCause(e)
                .build();
        }
    }

    @Override
    public MeShareableResponse create(String accessToken, CreateMeShareableRequest request)
        throws AuthorizationRestException, CreateMeShareableRestException {
        return getOrCreateSharable(accessToken, request, Optional.empty());
    }

    @Override
    public MeShareableResponse getOrCreate(String accessToken, CreateMeShareableRequest request)
        throws AuthorizationRestException, CreateMeShareableRestException {
        return getOrCreateSharable(accessToken, request, Optional.of(
            (personProfile, createRequest) -> lookForExistingShareable(personProfile,
                shareable -> shareableDeepEquals(shareable, createRequest))));
    }

    @Override
    public MeShareableResponse getCreateOrUpdate(String accessToken, CreateMeShareableRequest request)
        throws AuthorizationRestException, CreateMeShareableRestException {
        return getOrCreateSharable(accessToken, request, Optional.of(
            (personProfile, createRequest) -> lookForExistingShareable(personProfile,
                shareable -> shareableEquals(shareable, createRequest))));
    }

    private MeShareableResponse getOrCreateSharable(String accessToken, CreateMeShareableRequest request,
        Optional<BiFunction<Person, CreateMeShareableRequest, Optional<Shareable>>> existingShareableFinder)
        throws AuthorizationRestException, CreateMeShareableRestException {
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
            Shareable shareable = consumerEventSenderService
                .createInputEvent(authorization, processedRawEvent)
                .withLockDescription(new LockDescription("me-shareable-endpoints-create"))
                .executeAndSend((personBuilder, person, inputEventBuilder) -> {
                    try {
                        if (existingShareableFinder.isPresent()) {
                            Optional<Shareable> existingShareable =
                                existingShareableFinder.get().apply(person, request);
                            if (existingShareable.isPresent()) {
                                return new InputEventLockClosureResult<>(person, existingShareable.get());
                            }
                        }
                        ConsumerShareableBuilder builder = consumerShareableService
                            .create(processedRawEvent.getClientDomain(), personBuilder)
                            .withCheckKeyUniqueness(false);

                        if (request.getPreferredCodePrefixes() != null) {
                            builder.withPreferredCodePrefixes(request.getPreferredCodePrefixes());
                        }
                        if (request.getKey() != null) {
                            builder.withKey(request.getKey());
                        }
                        if (request.getLabel() != null) {
                            builder.withLabel(request.getLabel());
                        }

                        addContent(builder, request.getContent());
                        addData(builder, request.getData());

                        Person updatedPerson = builder.save();
                        Shareable createdShareable = builder.complete().getResult().get();

                        return new InputEventLockClosureResult<>(updatedPerson, createdShareable);
                    } catch (ShareableFieldLengthException | ShareableFieldValueException | ShareableCodeTakenException
                        | ShareableLabelIllegalCharacterInNameException | ShareableLabelNameLengthException
                        | ShareableDataAttributeNameInvalidException | ShareableDataAttributeValueInvalidException
                        | ShareableDataAttributeNameLengthException | ShareableDataAttributeValueLengthException
                        | ShareableKeyTakenException | ShareableBlockedUrlException
                        | ShareableContentDescriptionTooLongException | ShareableCodeTakenByPromotionException
                        | ShareableCodeReservedException | ShareableCodeBlacklistedException
                        | ShareableMissingProgramIdException | MalformedImageUrlException
                        | MalformedPartnerContentUrlException e) {
                        throw new LockClosureException(e);
                    }
                }).getPreEventSendingResult();

            ConsumerShareable consumerShareable =
                consumerShareableService.getByCode(authorization, shareable.getCode());
            return toMeShareableResponse(consumerShareable);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e).build();
        } catch (LockClosureException e) {
            Throwable cause = e.getCause();
            if (cause.getClass().isAssignableFrom(ShareableLabelNameLengthException.class)) {
                ShareableLabelNameLengthException exception = (ShareableLabelNameLengthException) cause;
                throw RestExceptionBuilder.newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.LABEL_NAME_LENGTH_OUT_OF_RANGE)
                    .addParameter("name", exception.getLabelName())
                    .addParameter("min_length", Integer.valueOf(exception.getMinLength()))
                    .addParameter("max_length", Integer.valueOf(exception.getMaxLength()))
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(ShareableLabelIllegalCharacterInNameException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.LABEL_NAME_CONTAINS_ILLEGAL_CHARACTER)
                    .addParameter("label", ((ShareableLabelIllegalCharacterInNameException) cause).getLabelName())
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(MalformedImageUrlException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.CONTENT_IMAGE_URL_INVALID)
                    .addParameter("image_url", request.getContent().getImageUrl())
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(MalformedPartnerContentUrlException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.CONTENT_URL_INVALID)
                    .addParameter("url", request.getContent().getUrl())
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(ShareableBlockedUrlException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.CONTENT_URL_BLOCKED)
                    .addParameter("url", request.getContent().getUrl())
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(ShareableDataAttributeNameInvalidException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.DATA_ATTRIBUTE_NAME_INVALID)
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(ShareableDataAttributeValueInvalidException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.DATA_ATTRIBUTE_VALUE_INVALID)
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(ShareableDataAttributeNameLengthException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.DATA_ATTRIBUTE_NAME_LENGTH_OUT_OF_RANGE)
                    .withCause(e).build();
            }
            if (cause.getClass().isAssignableFrom(ShareableDataAttributeValueLengthException.class)) {
                throw RestExceptionBuilder
                    .newBuilder(CreateMeShareableRestException.class)
                    .withErrorCode(CreateMeShareableRestException.DATA_ATTRIBUTE_VALUE_LENGTH_OUT_OF_RANGE)
                    .withCause(e).build();
            }
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(cause).build();
        } catch (AuthorizationException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    private Optional<Shareable> lookForExistingShareable(Person personProfile,
        Function<Shareable, Boolean> shareableComparator) {
        List<Shareable> existingShareables = personProfile.getShareables()
            .stream()
            .filter(shareable -> shareableComparator.apply(shareable))
            .collect(Collectors.toList());
        if (!existingShareables.isEmpty()) {
            return Optional.of(existingShareables.get(0));
        }

        return Optional.empty();
    }

    private static boolean shareableDeepEquals(Shareable shareable, CreateMeShareableRequest request) {
        if (shareable.getLabel().isPresent() && !shareable.getLabel().get().equals(request.getLabel())) {
            return false;
        }
        if (!Strings.isNullOrEmpty(shareable.getKey()) && !Strings.isNullOrEmpty(request.getKey()) &&
            !shareable.getKey().equals(request.getKey())) {
            return false;
        }
        if (shareable.getContent().isPresent() && !shareableContentDeepEquals(shareable.getContent().get(),
            request.getContent())) {
            return false;
        }
        if (!shareable.getData().equals(request.getData())) {
            return false;
        }
        return true;
    }

    private static boolean shareableEquals(Shareable shareable, CreateMeShareableRequest request) {
        if (shareable.getLabel().isPresent() && !shareable.getLabel().get().equals(request.getLabel())) {
            return false;
        }
        if (!Strings.isNullOrEmpty(shareable.getKey()) && !Strings.isNullOrEmpty(request.getKey()) &&
            !shareable.getKey().equals(request.getKey())) {
            return false;
        }
        return true;
    }

    private static boolean shareableContentDeepEquals(
        com.extole.person.service.shareable.ShareableContent profileShareable, ShareableContent shareableContent) {
        if (shareableContent == null) {
            return false;
        }
        if (profileShareable.getDescription().isPresent()
            && !profileShareable.getDescription().get().equals(shareableContent.getDescription())) {
            return false;
        }
        if (profileShareable.getPartnerContentId().isPresent()
            && !profileShareable.getPartnerContentId().get().equals(shareableContent.getPartnerContentId())) {
            return false;
        }
        if (profileShareable.getTitle().isPresent()
            && !profileShareable.getTitle().get().equals(shareableContent.getTitle())) {
            return false;
        }
        if (profileShareable.getUrl().isPresent()
            && !profileShareable.getUrl().get().toString().equals(shareableContent.getUrl())) {
            return false;
        }
        if (profileShareable.getImageUrl().isPresent()
            && !profileShareable.getImageUrl().get().toString().equals(shareableContent.getImageUrl())) {
            return false;
        }
        return true;
    }

    private static void addContent(ConsumerShareableBuilder builder, ShareableContent content) {
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

    private static void addData(ConsumerShareableBuilder builder, Map<String, String> data) {
        if (data.isEmpty()) {
            return;
        }
        builder.removeExistingData();
        for (Entry<String, String> dataEntry : data.entrySet()) {
            builder.addData(dataEntry.getKey(), dataEntry.getValue());
        }
    }

    private MeShareableResponse toMeShareableResponse(ConsumerShareable shareable) {
        return new MeShareableResponse(shareable.getCode(), shareable.getKey(), shareable.getLabel().orElse(null),
            shareable.getLink().toString(), toShareableContent(shareable.getContent()), shareable.getData());
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

    @Nullable
    static ShareableContent
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
        return new ShareableContent(partnerContentId, title, imageUrl, description, url);
    }
}
