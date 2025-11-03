package com.extole.consumer.rest.impl.share.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.lock.LockClosureException;
import com.extole.common.lock.LockDescription;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.share.event.ConsumerEventRequest;
import com.extole.consumer.rest.share.event.EventRestException;
import com.extole.consumer.rest.share.event.EventShareEndpoints;
import com.extole.consumer.rest.share.event.EventSharePollingResponse;
import com.extole.consumer.rest.share.event.EventShareRequest;
import com.extole.consumer.rest.share.event.EventShareResponse;
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
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.share.Channel;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;

@Deprecated // TODO remove ENG-10140
@Provider
public class EventShareEndpointsImpl implements EventShareEndpoints {

    private final ConsumerRequestContextService consumerRequestContextService;
    private final CustomShareService eventShareService;
    private final ShareableService shareableService;
    private final HttpServletRequest servletRequest;
    private final PersonService personService;

    private static final int MAX_SHARE_RECIPIENT_SIZE = 25;

    @Autowired
    public EventShareEndpointsImpl(ConsumerRequestContextService consumerRequestContextService,
        CustomShareService eventShareService,
        ShareableService shareableService,
        @Context HttpServletRequest servletRequest,
        PersonService personService) {
        this.consumerRequestContextService = consumerRequestContextService;
        this.eventShareService = eventShareService;
        this.shareableService = shareableService;
        this.servletRequest = servletRequest;
        this.personService = personService;
    }

    @Override
    public EventShareResponse shareEvent(String accessToken, EventShareRequest shareRequest)
        throws AuthorizationRestException, EventRestException {
        if (shareRequest.getShareableId() == null) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withErrorCode(EventRestException.SHAREABLE_ID_MISSING)
                .build();
        }

        try {
            Long.valueOf(shareRequest.getShareableId());
        } catch (NumberFormatException e) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withErrorCode(EventRestException.INVALID_SHAREABLE_ID)
                .addParameter("shareable_id", shareRequest.getShareableId())
                .withCause(e).build();
        }
        String channel = getChannel(shareRequest.getConsumerEvent().getChannelName());

        Map<String, String> parametersAndData = new HashMap<>();
        if (shareRequest.getConsumerEvent().getParameters() != null) {
            parametersAndData.putAll(shareRequest.getConsumerEvent().getParameters());
        }
        if (shareRequest.getData() != null) {
            parametersAndData.putAll(shareRequest.getData());
        }

        try {
            ConsumerEventRequest consumerEvent = shareRequest.getConsumerEvent();

            List<String> recipients = new ArrayList<>();
            if (shareRequest.getRecipientEmail() != null) {
                recipients.add(shareRequest.getRecipientEmail());
            } else {
                if (consumerEvent.getRecipientEmails() == null || consumerEvent.getRecipientEmails().isEmpty()) {
                    recipients.add(null); // send empty recipient
                } else {
                    recipients.addAll(consumerEvent.getRecipientEmails());
                }
            }
            if (recipients.size() > MAX_SHARE_RECIPIENT_SIZE) {
                throw RestExceptionBuilder.newBuilder(EventRestException.class)
                    .withErrorCode(EventRestException.INVALID_RECIPIENT_SIZE)
                    .build();
            }

            String campaignId = shareRequest.getCampaignId();
            if (campaignId == null) {
                campaignId = shareRequest.getConsumerEvent().getCampaignId();
            }
            PersonAuthorization authorization = null;

            List<ShareInputConsumerEventSendBuilder> shareBuilders = new ArrayList<>();
            for (String recipient : recipients) {
                ConsumerRequestContext context = consumerRequestContextService.createBuilder(servletRequest)
                    .withAccessToken(accessToken)
                    .withEventName(ConsumerEventName.SHARE.getEventName())
                    .withEventProcessing(configurator -> {
                        parametersAndData.forEach((key, value) -> {
                            configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                        });
                    })
                    .build();
                authorization = context.getAuthorization();
                Shareable shareable =
                    shareableService.get(authorization.getClientId(), Id.valueOf(shareRequest.getShareableId()));

                ShareInputConsumerEventSendBuilder shareBuilder = eventShareService
                    .createCustomShare(authorization, context.getProcessedRawEvent(), channel)
                    .withRecipient(recipient)
                    .withMessage(shareRequest.getMessage())
                    .withSource(consumerEvent.getSource())
                    .withCampaignId(campaignId);
                try {
                    shareBuilder.withShareable(shareable);
                } catch (AuthorizationException e) {
                    throw new ShareableNotFoundException(e.getMessage());
                }

                shareBuilders.add(shareBuilder);
            }

            return personService.execute(authorization, new LockDescription("consumer-api-v4-events-share"),
                (person) -> {
                    List<Id<?>> shareOperationIds = new ArrayList<>();

                    for (ShareInputConsumerEventSendBuilder shareBuilder : shareBuilders) {
                        try {
                            shareOperationIds.add(shareBuilder.send().getId());
                        } catch (PersonNotFoundException | EventShareMissingMessageException
                            | EventShareMissingRecipientException e) {
                            throw new LockClosureException(e);
                        }
                    }

                    Id<?> firstShareOperation = shareOperationIds.get(0);
                    return new EventShareResponse(firstShareOperation.getValue(), firstShareOperation.getValue());
                });
        } catch (EventShareMissingChannelException e) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withErrorCode(EventRestException.CHANNEL_MISSING)
                .withCause(e).build();
        } catch (InvalidRecipientException e) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withCause(e)
                .withErrorCode(EventRestException.INVALID_RECIPIENTS)
                .addParameter("recipient", e.getInvalidRecipient())
                .build();
        } catch (EventShareInvalidMessageLengthException e) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withErrorCode(EventRestException.INVALID_MESSAGE_LENGTH)
                .withCause(e).build();
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withErrorCode(EventRestException.SHAREABLE_NOT_FOUND)
                .addParameter("shareable_id", shareRequest.getShareableId())
                .withCause(e).build();
        } catch (EventShareInvalidMessageShareLinkException e) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withErrorCode(EventRestException.INVALID_SHARE_MESSAGE_LINK)
                .addParameter("link", e.getLink())
                .build();
        } catch (EventShareMessageForbiddenCharactersException e) {
            throw RestExceptionBuilder.newBuilder(EventRestException.class)
                .withCause(e)
                .withErrorCode(EventRestException.INVALID_SHARE_MESSAGE_CHARACTERS)
                .addParameter("forbidden_characters", e.getForbiddenCharacters())
                .addParameter("forbidden_characters_as_unicode", e.getForbiddenCharactersInUnicodeFormat())
                .addParameter("share_message", e.getShareMessage())
                .build();
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        } catch (LockClosureException e) {
            if (e.getCause() instanceof EventShareMissingRecipientException) {
                throw RestExceptionBuilder.newBuilder(EventRestException.class)
                    .withErrorCode(EventRestException.RECIPIENTS_MISSING)
                    .withCause(e)
                    .build();
            }
            if (e.getCause() instanceof EventShareMissingMessageException) {
                throw RestExceptionBuilder.newBuilder(EventRestException.class)
                    .withErrorCode(EventRestException.MESSAGE_MISSING)
                    .withCause(e)
                    .build();
            }

            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public EventSharePollingResponse shareEventStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        EventShareOperation pendingOperation = eventShareService.getStatus(authorization, Id.valueOf(pollingId));
        RestExceptionResponse errorResponse = null;
        if (pendingOperation.getStatus().isFailure()) {
            errorResponse = new RestExceptionResponseBuilder()
                .withUniqueId(pollingId)
                .withHttpStatusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .withCode("REWARD_CREATION_ERROR")
                .withMessage(pendingOperation.getError().getMessage())
                .withParameters(null)
                .build();
        }
        return new EventSharePollingResponse(pollingId,
            Optional.ofNullable(pendingOperation.getShareId()).map(Object::toString).orElse(null),
            EventSharePollingResponse.Status.valueOf(pendingOperation.getStatus().name()), errorResponse);
    }

    private String getChannel(String channel) {
        return Strings.isNullOrEmpty(channel) ? Channel.CHANNEL_CUSTOM_PURL_NAME : channel;
    }
}
