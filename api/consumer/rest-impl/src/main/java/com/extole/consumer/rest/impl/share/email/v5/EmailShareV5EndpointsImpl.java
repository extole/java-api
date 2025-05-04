package com.extole.consumer.rest.impl.share.email.v5;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.person.PersonAuthorization;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.share.custom.AdvocateCodeRestException;
import com.extole.consumer.rest.share.email.v5.BatchEmailRecipientV5RestException;
import com.extole.consumer.rest.share.email.v5.BatchEmailShareV5Request;
import com.extole.consumer.rest.share.email.v5.EmailRecipientV5RestException;
import com.extole.consumer.rest.share.email.v5.EmailShareContentV5RestException;
import com.extole.consumer.rest.share.email.v5.EmailShareV5Endpoints;
import com.extole.consumer.rest.share.email.v5.EmailShareV5Response;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.email.share.EmailShareService;
import com.extole.consumer.service.event.share.EventShareInvalidMessageLengthException;
import com.extole.consumer.service.event.share.EventShareInvalidMessageShareLinkException;
import com.extole.consumer.service.event.share.EventShareInvalidSubjectLengthException;
import com.extole.consumer.service.event.share.EventShareInvalidSubjectShareLinkException;
import com.extole.consumer.service.event.share.EventShareMessageForbiddenCharactersException;
import com.extole.consumer.service.event.share.EventShareMissingMessageException;
import com.extole.consumer.service.event.share.EventShareMissingRecipientException;
import com.extole.consumer.service.event.share.EventShareSubjectForbiddenCharactersException;
import com.extole.consumer.service.event.share.InvalidRecipientException;
import com.extole.consumer.service.event.share.ShareInputConsumerEventSendBuilder;
import com.extole.event.consumer.ConsumerEventName;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;

@Deprecated // TODO kept for zazzle-client only - ENG-18976
@Provider
public class EmailShareV5EndpointsImpl implements EmailShareV5Endpoints {
    private static final Logger LOG = LoggerFactory.getLogger(EmailShareV5EndpointsImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();

    private final HttpServletRequest servletRequest;
    private final EmailShareService shareService;
    private final ShareableService shareableService;
    private final ConsumerRequestContextService consumerRequestContextService;
    private static final int MAX_SHARE_RECIPIENT_SIZE = 100;
    // TODO ENG-11842 change it to lower case
    private static final String PARAMETER_BATCH_SHARE_EMAILS = "batchShareEmails";

    @Autowired
    public EmailShareV5EndpointsImpl(@Context HttpServletRequest servletRequest,
        EmailShareService shareService,
        ShareableService shareableService,
        ConsumerRequestContextService consumerRequestContextService) {
        this.servletRequest = servletRequest;
        this.shareService = shareService;
        this.shareableService = shareableService;
        this.consumerRequestContextService = consumerRequestContextService;
    }

    @Override
    public List<EmailShareV5Response> batchEmailShare(String accessToken, BatchEmailShareV5Request shareRequest)
        throws AuthorizationRestException, EmailRecipientV5RestException, EmailShareContentV5RestException,
        AdvocateCodeRestException, BatchEmailRecipientV5RestException {

        if (shareRequest.getRecipientEmails() == null || shareRequest.getRecipientEmails().isEmpty()) {
            throw RestExceptionBuilder.newBuilder(EmailRecipientV5RestException.class)
                .withErrorCode(EmailRecipientV5RestException.NO_RECIPIENT)
                .build();
        } else if (shareRequest.getRecipientEmails().size() > MAX_SHARE_RECIPIENT_SIZE) {
            throw RestExceptionBuilder.newBuilder(BatchEmailRecipientV5RestException.class)
                .withErrorCode(BatchEmailRecipientV5RestException.INVALID_RECIPIENT_SIZE)
                .build();
        }

        Map<String, String> data =
            shareRequest.getData() == null ? new HashMap<>() : new HashMap<>(shareRequest.getData());
        data.put(PARAMETER_BATCH_SHARE_EMAILS, getHashedEmails(shareRequest.getRecipientEmails()));

        try {

            if (Strings.isNullOrEmpty(shareRequest.getAdvocateCode())) {
                throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                    .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_MISSING)
                    .build();
            }

            List<String> recipientEmails = shareRequest.getRecipientEmails().stream()
                .map(email -> CharMatcher.whitespace().trimFrom(email)).collect(Collectors.toList());

            Map<ShareInputConsumerEventSendBuilder, String> shareBuilders = new LinkedHashMap<>(recipientEmails.size());
            for (String recipient : recipientEmails) {
                ConsumerRequestContext context = consumerRequestContextService.createBuilder(servletRequest)
                    .withAccessToken(accessToken)
                    .withEventName(ConsumerEventName.SHARE.getEventName())
                    .withEventProcessing(configurator -> {
                        data.forEach((key, value) -> {
                            configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                        });
                    })
                    .build();
                PersonAuthorization authorization = context.getAuthorization();
                Shareable shareable = shareableService.getByCode(authorization.getClientId(),
                    shareRequest.getAdvocateCode());
                ShareInputConsumerEventSendBuilder shareBuilder = shareService
                    .createEmailShare(authorization, context.getProcessedRawEvent())
                    .withShareable(shareable)
                    .withRecipient(recipient)
                    .withSubject(shareRequest.getSubject())
                    .withMessage(shareRequest.getMessage());
                shareBuilders.put(shareBuilder, recipient);
            }

            List<EmailShareV5Response> result = new ArrayList<>();
            for (Map.Entry<ShareInputConsumerEventSendBuilder, String> shareBuilderEntry : shareBuilders.entrySet()) {
                String eventId = shareBuilderEntry.getKey().send().getId().getValue();
                result.add(new EmailShareV5Response(shareBuilderEntry.getValue(), eventId));
            }
            return result;
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                .withCause(e)
                .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                .build();
        } catch (EventShareMissingRecipientException e) {
            throw RestExceptionBuilder.newBuilder(EmailRecipientV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailRecipientV5RestException.NO_RECIPIENT)
                .build();
        } catch (InvalidRecipientException e) {
            throw RestExceptionBuilder.newBuilder(EmailRecipientV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailRecipientV5RestException.INVALID_RECIPIENT)
                .addParameter("recipients", e.getInvalidRecipient())
                .build();
        } catch (EventShareInvalidSubjectLengthException e) {
            throw RestExceptionBuilder.newBuilder(EmailShareContentV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailShareContentV5RestException.INVALID_SUBJECT_LENGTH)
                .addParameter("subject", e.getSubject())
                .build();
        } catch (EventShareMissingMessageException e) {
            throw RestExceptionBuilder.newBuilder(EmailShareContentV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailShareContentV5RestException.MESSAGE_MISSING)
                .build();
        } catch (EventShareInvalidMessageLengthException e) {
            throw RestExceptionBuilder.newBuilder(EmailShareContentV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailShareContentV5RestException.INVALID_MESSAGE_LENGTH)
                .addParameter("email_message", e.getEmailMessage())
                .build();
        } catch (EventShareInvalidMessageShareLinkException e) {
            throw RestExceptionBuilder.newBuilder(EmailShareContentV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailShareContentV5RestException.INVALID_SHARE_MESSAGE_LINK)
                .addParameter("link", e.getLink())
                .addParameter("share_message", e.getShareMessage())
                .build();
        } catch (EventShareInvalidSubjectShareLinkException e) {
            throw RestExceptionBuilder.newBuilder(EmailShareContentV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailShareContentV5RestException.INVALID_SHARE_SUBJECT_LINK)
                .addParameter("link", e.getLink())
                .addParameter("subject", e.getSubject())
                .build();
        } catch (EventShareMessageForbiddenCharactersException e) {
            throw RestExceptionBuilder.newBuilder(EmailShareContentV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailShareContentV5RestException.INVALID_SHARE_MESSAGE_CHARACTERS)
                .addParameter("forbidden_characters", e.getForbiddenCharacters())
                .addParameter("forbidden_characters_as_unicode", e.getForbiddenCharactersInUnicodeFormat())
                .addParameter("share_message", e.getShareMessage())
                .build();
        } catch (EventShareSubjectForbiddenCharactersException e) {
            throw RestExceptionBuilder.newBuilder(EmailShareContentV5RestException.class)
                .withCause(e)
                .withErrorCode(EmailShareContentV5RestException.INVALID_SHARE_SUBJECT_CHARACTERS)
                .addParameter("forbidden_characters", e.getForbiddenCharacters())
                .addParameter("forbidden_characters_as_unicode", e.getForbiddenCharactersInUnicodeFormat())
                .addParameter("subject", e.getSubject())
                .build();
        } catch (ShareableNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_NOT_FOUND)
                .withCause(e)
                .addParameter("advocate_code", shareRequest.getAdvocateCode())
                .build();
        }
    }

    private String getHashedEmails(List<String> emails) throws BatchEmailRecipientV5RestException {
        List<String> hashedEmails = emails
            .stream()
            .map(email -> Hashing.md5().hashString(email, StandardCharsets.UTF_8).toString())
            .collect(Collectors.toList());
        try {
            return OBJECT_MAPPER.writeValueAsString(hashedEmails);
        } catch (IOException e) {
            String recipients = String.join(", ", emails);
            LOG.error("Can't convert email list to JSON " + recipients, e);
            throw RestExceptionBuilder.newBuilder(BatchEmailRecipientV5RestException.class)
                .withCause(e)
                .withErrorCode(BatchEmailRecipientV5RestException.RECIPIENT_JSON_CONVERSION_ERROR)
                .addParameter("recipients", recipients)
                .build();
        }
    }

}
