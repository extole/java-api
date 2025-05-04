package com.extole.client.rest.impl.email;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.ws.rs.ext.Provider;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.email.CnameRecord;
import com.extole.client.rest.email.CnameRecordValidationRestException;
import com.extole.client.rest.email.DnsRecordVerificationResponse;
import com.extole.client.rest.email.DomainValidationStatus;
import com.extole.client.rest.email.EmailDomainCreateRequest;
import com.extole.client.rest.email.EmailDomainEndpoints;
import com.extole.client.rest.email.EmailDomainResponse;
import com.extole.client.rest.email.EmailDomainRestException;
import com.extole.client.rest.email.EmailDomainUpdateRequest;
import com.extole.client.rest.email.EmailDomainValidationResponse;
import com.extole.client.rest.email.EmailDomainValidationRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.client.email.EmailDomain;
import com.extole.model.service.client.email.CnameRecordFieldTooLongException;
import com.extole.model.service.client.email.CnameRecordInvalidAliasException;
import com.extole.model.service.client.email.CnameRecordInvalidCanonicalNameException;
import com.extole.model.service.client.email.CnameRecordMissingRequiredFieldException;
import com.extole.model.service.client.email.DuplicateCnameRecordAliasException;
import com.extole.model.service.client.email.DuplicateEmailDomainException;
import com.extole.model.service.client.email.EmailDomainBuilder;
import com.extole.model.service.client.email.EmailDomainFieldTooLongException;
import com.extole.model.service.client.email.EmailDomainMissingRequiredFieldException;
import com.extole.model.service.client.email.EmailDomainNotFoundException;
import com.extole.model.service.client.email.EmailDomainService;
import com.extole.model.service.client.email.EmailDomainServiceException;
import com.extole.model.service.client.email.EmailDomainValidationException;
import com.extole.model.service.domain.DnsRecordVerificationResult;
import com.extole.model.service.domain.DomainValidationService;
import com.extole.model.service.domain.EmailDomainValidationResult;

@Provider
public class EmailDomainEndpointsImpl implements EmailDomainEndpoints {

    private final EmailDomainService emailDomainService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final DomainValidationService domainValidationService;

    @Autowired
    public EmailDomainEndpointsImpl(
        EmailDomainService emailDomainService,
        ClientAuthorizationProvider authorizationProvider,
        DomainValidationService domainValidationService) {
        this.emailDomainService = emailDomainService;
        this.authorizationProvider = authorizationProvider;
        this.domainValidationService = domainValidationService;
    }

    @Override
    public EmailDomainResponse create(String accessToken, EmailDomainCreateRequest request)
        throws UserAuthorizationRestException, EmailDomainValidationRestException, CnameRecordValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EmailDomainBuilder builder = emailDomainService.create(authorization)
                .withDomain(request.getDomain());

            request.getDkimCnameRecords().ifPresent(dkimCnameRecords -> {
                if (hasDkimRecords(dkimCnameRecords)) {
                    populateCnameRecords(dkimCnameRecords, builder);
                }
            });

            request.getForceSendFromEmailDomain().ifPresent(builder::withForceSendFromEmailDomain);

            return toResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        } catch (CnameRecordInvalidAliasException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.INVALID_ALIAS).withCause(e)
                .addParameter("value", e.getInvalidAlias())
                .build();
        } catch (CnameRecordInvalidCanonicalNameException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.INVALID_CANONICAL_NAME).withCause(e)
                .addParameter("value", e.getInvalidCanonicalName())
                .build();
        } catch (CnameRecordMissingRequiredFieldException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.MISSING_REQUIRED_FIELD).withCause(e)
                .addParameter("field_name", e.getFieldName())
                .build();
        } catch (CnameRecordFieldTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.TOO_LONG_FIELD).withCause(e)
                .addParameter("field_name", e.getFieldName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .build();
        } catch (EmailDomainMissingRequiredFieldException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.REQUIRED_DOMAIN_NAME).withCause(e)
                .build();
        } catch (EmailDomainFieldTooLongException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.TOO_LONG_FIELD).withCause(e)
                .addParameter("field_name", e.getFieldName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .build();
        } catch (EmailDomainValidationException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.INVALID_DOMAIN).withCause(e)
                .addParameter("value", e.getInvalidDomain())
                .addParameter("message", e.getMessage())
                .build();
        } catch (DuplicateEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.DUPLICATE_EMAIL_DOMAIN)
                .addParameter("email_domain", e.getEmailDomain().toString())
                .withCause(e)
                .build();
        } catch (DuplicateCnameRecordAliasException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.DUPLICATE_CNAME_ALIAS)
                .addParameter("alias", e.getAlias())
                .withCause(e)
                .build();
        } catch (EmailDomainServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public EmailDomainResponse get(String accessToken, String emailDomainId)
        throws UserAuthorizationRestException, EmailDomainRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EmailDomain existing = emailDomainService.get(authorization, Id.valueOf(emailDomainId));
            return toResponse(existing);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        } catch (EmailDomainNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainRestException.class)
                .withErrorCode(EmailDomainRestException.EMAIL_DOMAIN_NOT_FOUND)
                .addParameter("email_domain_id", e.getEmailDomainId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public List<EmailDomainResponse> list(String accessToken) throws UserAuthorizationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            return emailDomainService.findAll(authorization).stream()
                .map(emailDomain -> toResponse(emailDomain)).collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        }
    }

    @Override
    public EmailDomainResponse update(String accessToken, String emailDomainId, EmailDomainUpdateRequest request)
        throws UserAuthorizationRestException, EmailDomainRestException, EmailDomainValidationRestException,
        CnameRecordValidationRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);

        try {
            EmailDomainBuilder builder = emailDomainService.update(authorization, Id.valueOf(emailDomainId));

            request.getDomain().ifPresent(builder::withDomain);

            request.getDkimCnameRecords().ifPresent(dkimCnameRecords -> {
                if (dkimCnameRecords != null) {
                    builder.deleteExistingCnameRecords();
                }
                if (hasDkimRecords(dkimCnameRecords)) {
                    populateCnameRecords(dkimCnameRecords, builder);
                }
            });

            request.getForceSendFromEmailDomain().ifPresent(builder::withForceSendFromEmailDomain);

            return toResponse(builder.save());
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        } catch (EmailDomainNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainRestException.class)
                .withErrorCode(EmailDomainRestException.EMAIL_DOMAIN_NOT_FOUND)
                .addParameter("email_domain_id", e.getEmailDomainId())
                .withCause(e)
                .build();
        } catch (CnameRecordInvalidAliasException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.INVALID_ALIAS).withCause(e)
                .addParameter("value", e.getInvalidAlias())
                .build();
        } catch (CnameRecordInvalidCanonicalNameException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.INVALID_CANONICAL_NAME).withCause(e)
                .addParameter("value", e.getInvalidCanonicalName())
                .build();
        } catch (CnameRecordMissingRequiredFieldException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.MISSING_REQUIRED_FIELD).withCause(e)
                .addParameter("field_name", e.getFieldName())
                .build();
        } catch (CnameRecordFieldTooLongException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.TOO_LONG_FIELD).withCause(e)
                .addParameter("field_name", e.getFieldName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .build();
        } catch (EmailDomainMissingRequiredFieldException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.REQUIRED_DOMAIN_NAME).withCause(e)
                .build();
        } catch (EmailDomainFieldTooLongException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.TOO_LONG_FIELD).withCause(e)
                .addParameter("field_name", e.getFieldName())
                .addParameter("max_length", Integer.valueOf(e.getMaxLength()))
                .build();
        } catch (EmailDomainValidationException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.INVALID_DOMAIN).withCause(e)
                .addParameter("value", e.getInvalidDomain())
                .addParameter("message", e.getMessage())
                .build();
        } catch (DuplicateEmailDomainException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainValidationRestException.class)
                .withErrorCode(EmailDomainValidationRestException.DUPLICATE_EMAIL_DOMAIN)
                .addParameter("email_domain", e.getEmailDomain().toString())
                .withCause(e)
                .build();
        } catch (DuplicateCnameRecordAliasException e) {
            throw RestExceptionBuilder.newBuilder(CnameRecordValidationRestException.class)
                .withErrorCode(CnameRecordValidationRestException.DUPLICATE_CNAME_ALIAS)
                .addParameter("alias", e.getAlias())
                .withCause(e)
                .build();
        } catch (EmailDomainServiceException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR)
                .withCause(e)
                .build();
        }
    }

    @Override
    public EmailDomainResponse delete(String accessToken, String emailDomainId)
        throws UserAuthorizationRestException, EmailDomainRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EmailDomain deleted = emailDomainService.delete(authorization, Id.valueOf(emailDomainId));
            return toResponse(deleted);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        } catch (EmailDomainNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainRestException.class)
                .withErrorCode(EmailDomainRestException.EMAIL_DOMAIN_NOT_FOUND)
                .addParameter("email_domain_id", e.getEmailDomainId())
                .withCause(e)
                .build();
        }
    }

    @Override
    public EmailDomainValidationResponse validate(String accessToken, String emailDomainId)
        throws UserAuthorizationRestException, EmailDomainRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            EmailDomain domain = emailDomainService.get(authorization, Id.valueOf(emailDomainId));
            EmailDomainValidationResult verified = domainValidationService.validate(authorization, domain);

            return toResponse(verified);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED).withCause(e)
                .build();
        } catch (EmailDomainNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(EmailDomainRestException.class)
                .withErrorCode(EmailDomainRestException.EMAIL_DOMAIN_NOT_FOUND)
                .addParameter("email_domain_id", e.getEmailDomainId())
                .withCause(e)
                .build();
        }
    }

    private EmailDomainValidationResponse toResponse(EmailDomainValidationResult verified) {
        DnsRecordVerificationResponse spf = toResponse(verified.getSpfRecord());

        List<DnsRecordVerificationResponse> dkim = verified.getDkimRecords().stream()
            .map(record -> toResponse(record))
            .collect(Collectors.toList());

        DnsRecordVerificationResponse dmarc = toResponse(verified.getDmarcRecord());

        String emailDomain = verified.getEmailDomain().toString();

        return new EmailDomainValidationResponse(
            DomainValidationStatus.valueOf(verified.getDomainValidationStatus().name()), emailDomain, spf, dkim, dmarc,
            toResponse(verified.getARecord()), toResponse(verified.getMxRecord()));
    }

    private DnsRecordVerificationResponse toResponse(DnsRecordVerificationResult source) {
        DomainValidationStatus status = DomainValidationStatus.valueOf(source.getDomainValidationStatus().name());
        return new DnsRecordVerificationResponse(status, source.getRecord(),
            source.getDomainValidationReason());
    }

    private void populateCnameRecords(List<CnameRecord> records, EmailDomainBuilder builder)
        throws CnameRecordFieldTooLongException, CnameRecordMissingRequiredFieldException,
        CnameRecordInvalidAliasException, CnameRecordInvalidCanonicalNameException {
        for (CnameRecord record : records) {
            if (record != null) {
                builder.addDkimCnameRecord()
                    .withAlias(record.getAlias())
                    .withCanonicalName(record.getCanonicalName());
            }
        }
    }

    private EmailDomainResponse toResponse(EmailDomain emailDomain) {
        List<CnameRecord> records = emailDomain.getDkimCnameRecords().stream()
            .map(source -> new CnameRecord(source.getAlias(), source.getCanonicalName()))
            .collect(Collectors.toList());

        String domainId = emailDomain.getId().getValue();
        String domainName = emailDomain.getEmailDomain().toString();

        return new EmailDomainResponse(domainId, domainName, records, emailDomain.isForceSendFromEmailDomain());
    }

    private boolean hasDkimRecords(@Nullable List<CnameRecord> dkimRecords) {
        return CollectionUtils.emptyIfNull(dkimRecords).stream().anyMatch(record -> Objects.nonNull(record));
    }
}
