package com.extole.consumer.rest.impl.share.email;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
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
import com.extole.common.lang.JsonMap;
import com.extole.common.lang.ObjectMapperProvider;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.model.RestExceptionResponse;
import com.extole.common.rest.model.RestExceptionResponseBuilder;
import com.extole.consumer.event.service.processor.EventData;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.impl.request.context.ConsumerRequestContextService;
import com.extole.consumer.rest.share.email.AdvocateCodeRestException;
import com.extole.consumer.rest.share.email.BatchEmailRecipientRestException;
import com.extole.consumer.rest.share.email.BatchEmailShareRequest;
import com.extole.consumer.rest.share.email.BatchEmailShareWithAdvocateCodeRequest;
import com.extole.consumer.rest.share.email.EmailShareEndpoints;
import com.extole.consumer.rest.share.email.EmailSharePollingResponse;
import com.extole.consumer.rest.share.email.EmailShareRequest;
import com.extole.consumer.rest.share.email.EmailShareResponse;
import com.extole.consumer.rest.share.email.EmailShareRestException;
import com.extole.consumer.rest.share.email.EmailShareWithAdvocateCodeRequest;
import com.extole.consumer.service.ConsumerRequestContext;
import com.extole.consumer.service.email.share.EmailConsumerShareOperation;
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
import com.extole.id.Id;
import com.extole.person.service.profile.PersonService;
import com.extole.person.service.shareable.Shareable;
import com.extole.person.service.shareable.ShareableNotFoundException;
import com.extole.person.service.shareable.ShareableService;

@Provider
public class EmailShareEndpointsImpl implements EmailShareEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(EmailShareEndpointsImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();
    private static final int MAX_SHARE_RECIPIENT_SIZE = 25;
    // TODO ENG-11842 change it to lower case
    private static final String PARAMETER_BATCH_SHARE_EMAILS = "batchShareEmails";

    private final HttpServletRequest servletRequest;
    private final ConsumerRequestContextService consumerRequestContextService;
    private final EmailShareService shareService;
    private final ShareableService shareableService;
    private final PersonService personService;

    @Autowired
    public EmailShareEndpointsImpl(@Context HttpServletRequest servletRequest,
        EmailShareService shareService,
        ShareableService shareableService,
        PersonService personService,
        ConsumerRequestContextService consumerRequestContextService) {
        this.servletRequest = servletRequest;
        this.shareService = shareService;
        this.shareableService = shareableService;
        this.personService = personService;
        this.consumerRequestContextService = consumerRequestContextService;
    }

    @Override
    public EmailShareResponse emailShareAdvocateCode(String accessToken,
        EmailShareWithAdvocateCodeRequest shareRequest) throws AuthorizationRestException,
        EmailShareRestException, AdvocateCodeRestException {
        if (Strings.isNullOrEmpty(shareRequest.getAdvocateCode())) {
            throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_MISSING)
                .build();
        }

        ShareProcessor shareRequestProcessor = newShareProcessor(accessToken, shareRequest.getData());
        return shareRequestProcessor
            .withCampaignId(shareRequest.getCampaignId())
            .withSubject(shareRequest.getSubject())
            .withMessage(shareRequest.getMessage())
            .withRecipientEmail(shareRequest.getRecipientEmail())
            .withAdvocateCode(shareRequest.getAdvocateCode())
            .process();
    }

    @Override
    public EmailShareResponse emailShare(String accessToken, EmailShareRequest shareRequest)
        throws AuthorizationRestException, EmailShareRestException {
        return newShareProcessor(accessToken, shareRequest.getData())
            .withCampaignId(shareRequest.getCampaignId())
            .withSubject(shareRequest.getSubject())
            .withMessage(shareRequest.getMessage())
            .withRecipientEmail(shareRequest.getRecipientEmail())
            .withKey(shareRequest.getKey())
            .withLabels(shareRequest.getLabels())
            .withPreferredCodePrefixes(shareRequest.getPreferredCodePrefixes())
            .process();
    }

    @Override
    public List<EmailShareResponse> batchEmailShareAdvocateCode(String accessToken,
        BatchEmailShareWithAdvocateCodeRequest shareRequest)
        throws AuthorizationRestException, EmailShareRestException, BatchEmailRecipientRestException,
        AdvocateCodeRestException {

        if (Strings.isNullOrEmpty(shareRequest.getAdvocateCode())) {
            throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_MISSING)
                .build();
        }
        List<String> sanitizedRecipients = sanitizeRecipients(shareRequest.getRecipientEmails());

        List<EmailShareResponse> responses = new ArrayList<>();
        for (String recipient : sanitizedRecipients) {
            Map<String, Object> clientParameters = new HashMap<>();
            clientParameters.put(PARAMETER_BATCH_SHARE_EMAILS, getHashedEmails(sanitizedRecipients));
            responses.add(newShareProcessor(accessToken, shareRequest.getData())
                .withCampaignId(shareRequest.getCampaignId())
                .withSubject(shareRequest.getSubject())
                .withMessage(shareRequest.getMessage())
                .withRecipientEmail(recipient)
                .addData(clientParameters)
                .withAdvocateCode(shareRequest.getAdvocateCode())
                .process());
        }
        return responses;
    }

    @Override
    public List<EmailShareResponse> batchEmailShare(String accessToken, BatchEmailShareRequest shareRequest)
        throws AuthorizationRestException, EmailShareRestException, BatchEmailRecipientRestException {

        List<String> sanitizedRecipients = sanitizeRecipients(shareRequest.getRecipientEmails());

        List<EmailShareResponse> responses = new ArrayList<>();
        for (String recipient : sanitizedRecipients) {
            Map<String, Object> clientParameters = new HashMap<>();
            clientParameters.put(PARAMETER_BATCH_SHARE_EMAILS, getHashedEmails(sanitizedRecipients));
            responses.add(newShareProcessor(accessToken, shareRequest.getData())
                .withCampaignId(shareRequest.getCampaignId())
                .withSubject(shareRequest.getSubject())
                .withMessage(shareRequest.getMessage())
                .withRecipientEmail(recipient)
                .addData(clientParameters)
                .withKey(shareRequest.getKey())
                .withLabels(shareRequest.getLabels())
                .withPreferredCodePrefixes(shareRequest.getPreferredCodePrefixes())
                .process());
        }
        return responses;
    }

    @Override
    public EmailSharePollingResponse emailShareStatus(String accessToken, String pollingId)
        throws AuthorizationRestException {
        PersonAuthorization authorization = consumerRequestContextService.createBuilder(servletRequest)
            .withAccessToken(accessToken)
            .build()
            .getAuthorization();
        EmailConsumerShareOperation pendingOperation = shareService.getStatus(authorization, Id.valueOf(pollingId));
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
        return new EmailSharePollingResponse(pollingId, pollingId,
            EmailSharePollingResponse.Status.valueOf(pendingOperation.getStatus().name()), errorResponse);
    }

    private String getHashedEmails(List<String> emails) throws BatchEmailRecipientRestException {
        List<String> hashedEmails = emails
            .stream().map(email -> Hashing.md5().hashString(email, StandardCharsets.UTF_8).toString())
            .collect(Collectors.toList());
        try {
            return OBJECT_MAPPER.writeValueAsString(hashedEmails);
        } catch (IOException e) {
            String recipients = emails.stream().collect(Collectors.joining(", "));
            LOG.error("Can't convert email list to JSON {}", recipients, e);
            throw RestExceptionBuilder.newBuilder(BatchEmailRecipientRestException.class)
                .withCause(e)
                .withErrorCode(BatchEmailRecipientRestException.RECIPIENT_JSON_CONVERSION_ERROR)
                .addParameter("recipients", recipients)
                .build();
        }
    }

    private ShareProcessor newShareProcessor(String accessToken, Map<String, String> data)
        throws AuthorizationRestException {
        return new ShareProcessor(accessToken, data, servletRequest);
    }

    private List<String> sanitizeRecipients(List<String> recipientEmails)
        throws EmailShareRestException, BatchEmailRecipientRestException {
        if (recipientEmails == null || recipientEmails.isEmpty()) {
            throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                .withErrorCode(EmailShareRestException.NO_RECIPIENT)
                .build();
        } else if (recipientEmails.size() > MAX_SHARE_RECIPIENT_SIZE) {
            throw RestExceptionBuilder.newBuilder(BatchEmailRecipientRestException.class)
                .withErrorCode(BatchEmailRecipientRestException.INVALID_RECIPIENT_SIZE)
                .build();
        }
        return recipientEmails.stream()
            .filter(email -> email != null)
            .map(email -> CharMatcher.whitespace().trimFrom(email)).collect(Collectors.toList());
    }

    private final class ShareProcessor {
        private final ConsumerRequestContext context;
        private String message;
        private String subject;
        private String recipientEmail;
        private Map<String, Object> data;
        private List<String> preferredCodePrefixes;
        private String key;
        private String labels;
        private String campaignId;
        private Shareable shareable;

        private ShareProcessor(String accessToken, Map<String, String> data, HttpServletRequest servletRequest)
            throws AuthorizationRestException {
            this.context = consumerRequestContextService.createBuilder(servletRequest)
                .withAccessToken(accessToken)
                .withEventName(ConsumerEventName.SHARE.getEventName())
                .withEventProcessing(configurator -> {
                    data.forEach((key, value) -> {
                        configurator.addData(new EventData(key, value, EventData.Source.REQUEST_BODY, false, true));
                    });
                })
                .build();
        }

        public ShareProcessor withMessage(String message) {
            this.message = message;
            return this;
        }

        ShareProcessor withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        ShareProcessor withRecipientEmail(String recipientEmail) {
            if (recipientEmail != null) {
                this.recipientEmail = CharMatcher.whitespace().trimFrom(recipientEmail);
            }
            return this;
        }

        ShareProcessor withPreferredCodePrefixes(List<String> preferredCodePrefixes) {
            this.preferredCodePrefixes = preferredCodePrefixes;
            return this;
        }

        ShareProcessor withKey(String key) {
            this.key = key;
            return this;
        }

        ShareProcessor withLabels(String labels) {
            this.labels = labels;
            return this;
        }

        ShareProcessor withCampaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        ShareProcessor addData(Map<String, Object> data) {
            if (data == null) {
                return this;
            }
            if (this.data == null) {
                this.data = new HashMap<>();
            }
            this.data.putAll(data);
            return this;
        }

        ShareProcessor withAdvocateCode(String advocateCode) throws AdvocateCodeRestException {
            PersonAuthorization authorization = context.getAuthorization();
            try {
                shareable = shareableService.getByCode(authorization.getClientId(), advocateCode);
            } catch (ShareableNotFoundException e) {
                throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                    .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_NOT_FOUND)
                    .addParameter("advocate_code", advocateCode)
                    .withCause(e)
                    .build();
            }
            if (!personService.isSamePerson(authorization.getClientId(),
                authorization.getIdentityId(),
                shareable.getPersonId())) {
                throw RestExceptionBuilder.newBuilder(AdvocateCodeRestException.class)
                    .withErrorCode(AdvocateCodeRestException.ADVOCATE_CODE_NOT_FOUND)
                    .addParameter("advocate_code", advocateCode)
                    .build();
            }
            return this;
        }

        EmailShareResponse process() throws AuthorizationRestException, EmailShareRestException {
            PersonAuthorization authorization = context.getAuthorization();
            try {
                String sourceData = null;
                if (data != null) {
                    sourceData = JsonMap.valueOf(data).getValueAsString("source").orElse(null);
                }

                ShareInputConsumerEventSendBuilder shareBuilder =
                    shareService.createEmailShare(authorization, context.getProcessedRawEvent());

                if (campaignId != null) {
                    shareBuilder.withCampaignId(campaignId);
                }

                if (shareable != null) {
                    shareBuilder.withShareable(shareable);
                } else {
                    if (preferredCodePrefixes != null) {
                        shareBuilder.withPreferredCodePrefixes(preferredCodePrefixes);
                    }
                    if (labels != null) {
                        shareBuilder.withLabels(labels);
                    }
                    if (key != null) {
                        shareBuilder.withKey(key);
                    }
                }

                String id = shareBuilder
                    .withSource(sourceData)
                    .withRecipient(recipientEmail)
                    .withSubject(subject)
                    .withMessage(message)
                    .addData(data)
                    .send().getId().getValue();
                return new EmailShareResponse(recipientEmail, id);
            } catch (AuthorizationException e) {
                throw RestExceptionBuilder.newBuilder(AuthorizationRestException.class)
                    .withErrorCode(AuthorizationRestException.ACCESS_DENIED)
                    .withCause(e)
                    .build();
            } catch (EventShareMissingRecipientException e) {
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withCause(e)
                    .withErrorCode(EmailShareRestException.NO_RECIPIENT)
                    .build();
            } catch (InvalidRecipientException e) {
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withErrorCode(EmailShareRestException.INVALID_RECIPIENT)
                    .addParameter("recipients", e.getInvalidRecipient())
                    .withCause(e)
                    .build();
            } catch (EventShareInvalidSubjectLengthException e) {
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withCause(e)
                    .withErrorCode(EmailShareRestException.INVALID_SUBJECT_LENGTH)
                    .addParameter("subject", e.getSubject())
                    .build();
            } catch (EventShareMissingMessageException e) {
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withCause(e)
                    .withErrorCode(EmailShareRestException.MESSAGE_MISSING)
                    .build();
            } catch (EventShareInvalidMessageLengthException e) {
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withCause(e)
                    .withErrorCode(EmailShareRestException.INVALID_MESSAGE_LENGTH)
                    .addParameter("email_message", e.getEmailMessage())
                    .build();
            } catch (EventShareInvalidMessageShareLinkException e) {
                String shareableInfo = "";
                if (shareable != null) {
                    shareableInfo = " shareable: " + shareable.getCode();
                }
                LOG.warn("Share message link deemed invalid for client " + authorization.getClientId() + shareableInfo
                    + " message was: " + message + ". link flagged: " + e.getLink());
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withErrorCode(EmailShareRestException.INVALID_SHARE_MESSAGE_LINK)
                    .addParameter("link", e.getLink())
                    .addParameter("share_message", e.getShareMessage())
                    .withCause(e)
                    .build();
            } catch (EventShareInvalidSubjectShareLinkException e) {
                String shareableInfo = "";
                if (shareable != null) {
                    shareableInfo = " shareable: " + shareable.getCode();
                }
                LOG.warn("Share subject link deemed invalid for client " + authorization.getClientId() + shareableInfo
                    + " subject was: " + subject + ". link flagged: " + e.getLink());
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withErrorCode(EmailShareRestException.INVALID_SHARE_SUBJECT_LINK)
                    .addParameter("link", e.getLink())
                    .addParameter("subject", e.getSubject())
                    .withCause(e)
                    .build();
            } catch (EventShareMessageForbiddenCharactersException e) {
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withCause(e)
                    .withErrorCode(EmailShareRestException.INVALID_SHARE_MESSAGE_CHARACTERS)
                    .addParameter("forbidden_characters", e.getForbiddenCharacters())
                    .addParameter("forbidden_characters_as_unicode", e.getForbiddenCharactersInUnicodeFormat())
                    .addParameter("share_message", e.getShareMessage())
                    .build();
            } catch (EventShareSubjectForbiddenCharactersException e) {
                throw RestExceptionBuilder.newBuilder(EmailShareRestException.class)
                    .withCause(e)
                    .withErrorCode(EmailShareRestException.INVALID_SHARE_SUBJECT_CHARACTERS)
                    .addParameter("forbidden_characters", e.getForbiddenCharacters())
                    .addParameter("forbidden_characters_as_unicode", e.getForbiddenCharactersInUnicodeFormat())
                    .addParameter("subject", e.getSubject())
                    .build();
            }
        }
    }
}
