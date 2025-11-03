package com.extole.consumer.rest.impl.share.custom;

import java.util.Collections;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.share.custom.AdvocateCodeRestException;
import com.extole.consumer.rest.share.custom.CustomShareEndpoints;
import com.extole.consumer.rest.share.custom.CustomSharePollingResponse;
import com.extole.consumer.rest.share.custom.CustomShareRequest;
import com.extole.consumer.rest.share.custom.CustomShareResponse;
import com.extole.consumer.rest.share.custom.CustomShareRestException;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.event.share.CustomShareService;
import com.extole.consumer.service.event.share.EventShareInvalidMessageLengthException;
import com.extole.consumer.service.event.share.EventShareInvalidMessageShareLinkException;
import com.extole.consumer.service.event.share.EventShareMessageForbiddenCharactersException;
import com.extole.consumer.service.event.share.EventShareMissingChannelException;
import com.extole.consumer.service.event.share.EventShareMissingMessageException;
import com.extole.consumer.service.event.share.EventShareMissingRecipientException;
import com.extole.consumer.service.event.share.EventShareOperation;
import com.extole.consumer.service.event.share.InvalidRecipientException;
import com.extole.consumer.service.event.share.ShareInputConsumerEventSendBuilder;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;
import com.extole.person.service.share.Channel;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;

@Provider
public class CustomShareEndpointsImpl implements CustomShareEndpoints {

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final CustomShareService customShareService;
    private final ShareableService shareableService;

    @Autowired
    public CustomShareEndpointsImpl(@Context HttpServletRequest servletRequest,
        ConsumerRequestContextService consumerRequestContextService,
        CustomShareService customShareService,
        ShareableService shareableService) {
        this.servletRequest = servletRequest;
        this.consumerRequestContextService = consumerRequestContextService;
        this.customShareService = customShareService;
        this.shareableService = shareableService;
    }

    @Override
    public CustomShareResponse customShare(String accessToken, CustomShareRequest shareRequest)
        throws AuthorizationRestException, CustomShareRestException, AdvocateCodeRestException {
        ConsumerRequestContext context = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .withEventName(ConsumerEventName.SHARE.getEventName())
            .withEventProcessing(configurator -> {
                shareRequest.getData().orElse(Collections.emptyMap()).forEach((key, value) -> {
                    configurator.addData(
                        new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                });
            })
            .build();
        PersonAuthorization authorization = context.getAuthorization();
        try {
            if (Strings.isNullOrEmpty(shareRequest.getAdvocateCode())) {
                throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                    .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_MISSING)
                    .build();
            }
            Shareable shareable =
                shareableService.getByCode(authorization.getClientId(), shareRequest.getAdvocateCode());
            String sourceData = null;
            if (shareRequest.getData().isPresent()) {
                sourceData = shareRequest.getData().get().get("source");
            }

            ShareInputConsumerEventSendBuilder shareBuilder = customShareService
                .createCustomShare(authorization, context.getProcessedRawEvent(), getChannel(shareRequest.getChannel()))
                .withSource(sourceData)
                .withRecipient(shareRequest.getRecipientEmail().orElse(null))
                .withMessage(shareRequest.getMessage().orElse(null));
            try {
                shareBuilder.withShareable(shareable);
            } catch (AuthorizationException e) {
                throw new ShareableNotFoundException(e.getMessage());
            }
            String id = shareBuilder.send().getId().getValue();
            return new CustomShareResponse(id, id);
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_NOT_FOUND)
                .addParameter("advocate_code", shareRequest.getAdvocateCode())
                .withCause(e).build();
        } catch (InvalidRecipientException e) {
            throw RestExceptionBuilder.newBuilder(CustomShareRestException.class)
                .withErrorCode(CustomShareRestException.INVALID_RECIPIENT)
                .addParameter("recipient", e.getInvalidRecipient())
                .withCause(e)
                .build();
        } catch (EventShareInvalidMessageLengthException e) {
            throw RestExceptionBuilder.newBuilder(CustomShareRestException.class)
                .withErrorCode(CustomShareRestException.INVALID_MESSAGE_LENGTH)
                .withCause(e).build();
        } catch (EventShareInvalidMessageShareLinkException e) {
            throw RestExceptionBuilder.newBuilder(CustomShareRestException.class)
                .withErrorCode(CustomShareRestException.INVALID_SHARE_MESSAGE_LINK)
                .addParameter("link", e.getLink())
                .withCause(e).build();
        } catch (EventShareMessageForbiddenCharactersException e) {
            throw RestExceptionBuilder.newBuilder(CustomShareRestException.class)
                .withCause(e)
                .withErrorCode(CustomShareRestException.INVALID_SHARE_MESSAGE_CHARACTERS)
                .addParameter("forbidden_characters", e.getForbiddenCharacters())
                .addParameter("forbidden_characters_as_unicode", e.getForbiddenCharactersInUnicodeFormat())
                .addParameter("share_message", e.getShareMessage())
                .build();
        } catch (EventShareMissingChannelException | EventShareMissingMessageException
            | EventShareMissingRecipientException | AuthorizationException | PersonNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public CustomSharePollingResponse customShareStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        EventShareOperation pendingOperation = customShareService.getStatus(authorization, Id.valueOf(pollingId));
        RestExceptionResponse errorResponse = null;
        if (pendingOperation.getStatus().isFailure()) {
            errorResponse = new RestExceptionResponseBuilder()
                .withUniqueId(pollingId)
                .withHttpStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .withCode("SHARE_REQUEST_FAILED")
                .withMessage(pendingOperation.getError().getMessage())
                .withParameters(null)
                .build();
        }
        return new CustomSharePollingResponse(pollingId,
            Optional.ofNullable(pendingOperation.getShareId()).map(Object::toString).orElse(null),
            CustomSharePollingResponse.Status.valueOf(pendingOperation.getStatus().name()), errorResponse);
    }

    private String getChannel(String channel) {
        return Strings.isNullOrEmpty(channel) ? Channel.CHANNEL_CUSTOM_PURL_NAME : channel;
    }
}
