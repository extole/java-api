package com.extole.client.rest.impl.optout;

import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.ClientHandle;
import com.extole.client.rest.optout.OptoutEndpoints;
import com.extole.client.rest.optout.OptoutResponse;
import com.extole.client.rest.optout.OptoutRestException;
import com.extole.common.rest.exception.FatalRestRuntimeException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.optout.external.ExternalOptoutClientException;
import com.extole.optout.external.OptoutService;

@Provider
public class OptoutEndpointsImpl implements OptoutEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final OptoutService optoutService;

    @Autowired
    public OptoutEndpointsImpl(ClientAuthorizationProvider authorizationProvider,
        OptoutService optoutService) {
        this.authorizationProvider = authorizationProvider;
        this.optoutService = optoutService;
    }

    @Override
    public OptoutResponse isOptout(String accessToken, String email)
        throws UserAuthorizationRestException, OptoutRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        validateEmail(email);
        Boolean isOptout = isOptout(userAuthorization.getClientId(), email);
        return new OptoutResponse(email, isOptout);
    }

    private Boolean isOptout(Id<ClientHandle> clientId, String email) {
        try {
            return Boolean.valueOf(optoutService.isOptout(clientId, email));
        } catch (ExternalOptoutClientException e) {
            throw RestExceptionBuilder.newBuilder(FatalRestRuntimeException.class)
                .withErrorCode(FatalRestRuntimeException.SOFTWARE_ERROR).withCause(e)
                .build();
        }
    }

    private void validateEmail(String email) throws OptoutRestException {
        if (Strings.isNullOrEmpty(email)) {
            throw RestExceptionBuilder.newBuilder(OptoutRestException.class)
                .withErrorCode(OptoutRestException.MISSING_EMAIL_ADDRESS)
                .build();
        }
    }
}
