package com.extole.api.impl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extole.api.service.EmailVerification;
import com.extole.api.service.EmailVerificationService;
import com.extole.email.provider.service.InvalidEmailAddress;
import com.extole.email.provider.service.InvalidEmailDomainException;
import com.extole.email.provider.service.VerifiedEmail;
import com.extole.email.provider.service.VerifiedEmailService;

public class EmailVerificationServiceImpl implements EmailVerificationService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    private final VerifiedEmailService verifiedEmailService;

    public EmailVerificationServiceImpl(VerifiedEmailService verifiedEmailService) {
        this.verifiedEmailService = verifiedEmailService;
    }

    @Override
    public boolean isEmailValid(String email) {
        return verifiedEmailService.isEmailValid(email);
    }

    @Override
    public EmailVerification verifyEmail(String email) {
        try {
            VerifiedEmail result = verifiedEmailService.verifyEmail(email);
            return EmailVerificationImpl.valid(new VerifiedEmailImpl(result.getEmail()));
        } catch (InvalidEmailAddress | InvalidEmailDomainException e) {
            LOG.debug("Verification of email: {} failed due to: {}", email, e.toString());
            return EmailVerificationImpl.invalid();
        }
    }
}
