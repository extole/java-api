package com.extole.consumer.rest.impl.email;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.consumer.rest.email.EmailProvider;
import com.extole.consumer.rest.email.EmailProviderEndpoints;
import com.extole.consumer.rest.email.EmailProviderRestException;
import com.extole.consumer.rest.email.EmailProviderValidationRequest;
import com.extole.consumer.rest.email.EmailProviderValidationResponse;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmail;
import com.extole.email.provider.service.VerifiedEmailService;

@Provider
public class EmailProviderEndpointsImpl implements EmailProviderEndpoints {
    private static final Logger LOG = LoggerFactory.getLogger(EmailProviderEndpointsImpl.class);

    private final VerifiedEmailService verifiedEmailService;

    @Inject
    public EmailProviderEndpointsImpl(VerifiedEmailService verifiedEmailService) {
        this.verifiedEmailService = verifiedEmailService;
    }

    @Override
    public EmailProviderValidationResponse validate(EmailProviderValidationRequest request)
        throws EmailProviderRestException {
        String email = request.getEmail();
        try {
            VerifiedEmail verifiedEmail = verifiedEmailService.verifyEmail(email);
            LOG.debug("Found email provider {} for email {}", verifiedEmail.getProvider(), email);
            return new EmailProviderValidationResponse(verifiedEmail.getEmail().getNormalizedAddress(),
                EmailProvider.valueOf(verifiedEmail.getProvider().toString()));
        } catch (InvalidEmailAddress e) {
            LOG.debug("Email {} is not valid", email);
            throw RestExceptionBuilder.newBuilder(EmailProviderRestException.class)
                .withErrorCode(EmailProviderRestException.INVALID_EMAIL)
                .addParameter("email", email)
                .withCause(e).build();
        } catch (InvalidEmailDomainException e) {
            LOG.debug("Email {} has an invalid domain", email, e.getMessage());
            throw RestExceptionBuilder.newBuilder(EmailProviderRestException.class)
                .withErrorCode(EmailProviderRestException.INVALID_DOMAIN)
                .addParameter("domain", e.getMessage())
                .withCause(e).build();
        }
    }
}
