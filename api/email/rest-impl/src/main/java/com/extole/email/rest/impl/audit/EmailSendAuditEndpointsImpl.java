package com.extole.email.rest.impl.audit;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.email.rest.audit.EmailSendAddressRestException;
import com.extole.email.rest.audit.EmailSendAuditEndpoints;
import com.extole.email.rest.audit.EmailSendResponse;
import com.extole.email.rest.audit.EmailSendRestException;
import com.extole.email.rest.audit.EmailSendResultResponse;
import com.extole.email.rest.audit.RecipientType;
import com.extole.email.service.audit.EmailSendAddressException;
import com.extole.email.service.audit.EmailSendAuditFilterBuilder;
import com.extole.email.service.audit.EmailSendAuditService;
import com.extole.email.service.audit.EmailSendNotFoundException;
import com.extole.email.service.person.EmailSend;
import com.extole.email.service.person.EmailSendResult;
import com.extole.id.Id;
import com.extole.person.service.profile.PersonNotFoundException;

@Provider
public class EmailSendAuditEndpointsImpl implements EmailSendAuditEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(EmailSendAuditEndpoints.class);
    private static final int DEFAULT_EMAIL_LIMIT = 100;
    private final ClientAuthorizationProvider authorizationProvider;

    private final EmailSendAuditService emailSendAuditService;

    @Inject
    public EmailSendAuditEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        EmailSendAuditService emailSendAuditService) {
        this.authorizationProvider = authorizationProvider;
        this.emailSendAuditService = emailSendAuditService;
    }

    @Override
    public EmailSendResponse getEmailByEmailId(String accessToken, String emailId, ZoneId timeZone)
        throws UserAuthorizationRestException, EmailSendRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EmailSend emailSend =
                emailSendAuditService.getEmailSendByEmailId(authorization, Id.valueOf(emailId));
            return toResponse(emailSend, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EmailSendNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EmailSendRestException.class)
                .withErrorCode(EmailSendRestException.EMAIL_RESPONSE_NOT_FOUND)
                .addParameter("email_id", emailId)
                .withCause(e).build();
        }
    }

    @Override
    public List<EmailSendResponse> getEmails(String accessToken, String causeId, String personId, String email,
        String limitParam, ZoneId timeZone)
        throws UserAuthorizationRestException, EmailSendAddressRestException {

        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        int limit = getLimit(limitParam, s -> LOG.debug(
            "Invalid limit value of {} Setting to default of {}. causeId = {}, personId = {}, clientId = {}",
            limitParam, DEFAULT_EMAIL_LIMIT, causeId, personId, authorization.getClientId().getValue()));

        try {
            EmailSendAuditFilterBuilder filterBuilder = emailSendAuditService.getEmailSends(authorization);

            List<EmailSend> emailSends = filterBuilder.withCauseId(causeId).withPersonId(personId)
                .withEmail(email).withLimit(limit).execute();

            return toResponseList(emailSends, timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EmailSendAddressException e) {
            throw RestExceptionBuilder.newBuilder(EmailSendAddressRestException.class)
                .withErrorCode(EmailSendAddressRestException.EMAIL_ADDRESS_INVALID)
                .addParameter("address", email)
                .withCause(e).build();
        } catch (PersonNotFoundException e) {
            // ignore exception, empty list will be returned
            return Collections.emptyList();
        }
    }

    @Override
    public List<EmailSendResultResponse> getEmailResultsByEmailId(String accessToken, String emailId, ZoneId timeZone)
        throws UserAuthorizationRestException, EmailSendRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EmailSend emailSend =
                emailSendAuditService.getEmailSendByEmailId(authorization, Id.valueOf(emailId));
            return toResponse(emailSend.getResults(), timeZone);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e).build();
        } catch (EmailSendNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EmailSendRestException.class)
                .withErrorCode(EmailSendRestException.EMAIL_RESPONSE_NOT_FOUND)
                .addParameter("email_id", emailId)
                .withCause(e).build();
        }
    }

    private EmailSendResponse toResponse(EmailSend emailSend, ZoneId timeZone) {
        EmailSendResultResponse resultResponse = null;
        List<EmailSendResult> results = emailSend.getResults();
        if (!results.isEmpty()) {
            resultResponse = toResponse(results.get(0), timeZone);
        }
        return new EmailSendResponse(emailSend.getEmailId().getValue(), emailSend.getSourceId().getValue(),
            emailSend.getClientId().getValue(), emailSend.getSenderId().getValue(),
            emailSend.getCreatedDate().atZone(timeZone), emailSend.getZoneName().orElse(null),
            Optional.ofNullable(emailSend.getProgramId()).map(Id::getValue).orElse(null),
            results.size(), emailSend.getData(), emailSend.getAttachments(), resultResponse);
    }

    private EmailSendResultResponse toResponse(EmailSendResult result, ZoneId timeZone) {
        return new EmailSendResultResponse(EmailSendResultResponse.Status.valueOf(result.getStatus().name()),
            result.getCreatedDate().atZone(timeZone),
            result.getNormalizedEmailFrom().orElse(null),
            result.getNormalizedEmailTo().orElse(null),
            result.getNormalizedEmailSender().orElse(null),
            result.getEmailSentAs().orElse(null),
            RecipientType.valueOf(result.getRecipientType().name()));
    }

    private List<EmailSendResultResponse> toResponse(List<EmailSendResult> results, ZoneId timeZone) {
        return results.stream().map(emailSend -> toResponse(emailSend, timeZone)).collect(Collectors.toList());
    }

    private List<EmailSendResponse> toResponseList(List<EmailSend> results, ZoneId timeZone) {
        return results.stream().map(emailSend -> toResponse(emailSend, timeZone)).collect(Collectors.toList());
    }

    private int getLimit(String limitParam, Consumer<Void> invalidLogConsumer) {
        int limit;

        if (Strings.isNullOrEmpty(limitParam)) {
            limit = DEFAULT_EMAIL_LIMIT;
        } else {
            try {
                limit = Integer.parseInt(limitParam);
                if (limit <= 0) {
                    invalidLogConsumer.accept(null);
                    limit = DEFAULT_EMAIL_LIMIT;
                }
            } catch (NumberFormatException e) {
                invalidLogConsumer.accept(null);
                limit = DEFAULT_EMAIL_LIMIT;
            }
        }

        return limit;
    }
}
