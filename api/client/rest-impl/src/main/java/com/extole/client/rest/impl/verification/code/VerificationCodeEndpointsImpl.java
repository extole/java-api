package com.extole.client.rest.impl.verification.code;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.verification.code.VerificationCodeEndpoints;
import com.extole.client.rest.verification.code.VerificationCodeResponse;
import com.extole.client.rest.verification.code.VerificationCodeRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.service.verification.code.VerificationCode;
import com.extole.model.service.verification.code.VerificationCodeNotFoundException;
import com.extole.model.service.verification.code.VerificationCodeService;

@Provider
public class VerificationCodeEndpointsImpl implements VerificationCodeEndpoints {
    private final VerificationCodeService verificationCodeService;
    private final ClientAuthorizationProvider authorizationProvider;

    @Inject
    public VerificationCodeEndpointsImpl(
        VerificationCodeService verificationCodeService,
        ClientAuthorizationProvider authorizationProvider) {
        this.verificationCodeService = verificationCodeService;
        this.authorizationProvider = authorizationProvider;
    }

    @Override
    public VerificationCodeResponse create(String accessToken) throws UserAuthorizationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            VerificationCode code = verificationCodeService.create(authorization);
            return new VerificationCodeResponse(code.getId().getValue(), true);
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        }
    }

    @Override
    public VerificationCodeResponse verify(String accessToken, String codeString) throws UserAuthorizationRestException,
        VerificationCodeRestException {
        authorizationProvider.getClientAuthorization(accessToken);
        if (Strings.isNullOrEmpty(codeString)) {
            throw RestExceptionBuilder.newBuilder(VerificationCodeRestException.class)
                .withErrorCode(VerificationCodeRestException.VERIFICATION_CODE_NOT_PROVIDED)
                .build();
        }
        try {
            Id<VerificationCode> verificationCodeId = Id.valueOf(codeString);
            boolean isValid = verificationCodeService.isValid(verificationCodeId);
            return new VerificationCodeResponse(codeString, isValid);
        } catch (VerificationCodeNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(VerificationCodeRestException.class)
                .withErrorCode(VerificationCodeRestException.VERIFICATION_CODE_NOT_FOUND)
                .addParameter("bad_code", codeString)
                .withCause(e)
                .build();
        }
    }
}
