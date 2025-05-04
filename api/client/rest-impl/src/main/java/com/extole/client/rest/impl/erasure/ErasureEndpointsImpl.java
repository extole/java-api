package com.extole.client.rest.impl.erasure;

import java.time.ZoneId;

import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.extole.authorization.service.Authorization;
import com.extole.authorization.service.AuthorizationException;
import com.extole.client.rest.erasure.ErasureEndpoints;
import com.extole.client.rest.erasure.ErasureRequest;
import com.extole.client.rest.erasure.ErasureResponse;
import com.extole.client.rest.erasure.ErasureStatus;
import com.extole.client.rest.erasure.ErasureValidationRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.erasure.Erasure;
import com.extole.erasure.ErasureInvalidEmailAddressException;
import com.extole.erasure.ErasureService;

@Provider
// TODO after ENG-9406, GET endpoints should be adjusted to return the created_at field in the desired timezone
public class ErasureEndpointsImpl implements ErasureEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final ErasureService erasureService;

    @Autowired
    public ErasureEndpointsImpl(ClientAuthorizationProvider authorizationProvider, ErasureService erasureService) {
        this.authorizationProvider = authorizationProvider;
        this.erasureService = erasureService;
    }

    @Override
    public ErasureResponse erase(String accessToken, ErasureRequest request, ZoneId timeZone)
        throws UserAuthorizationRestException, ErasureValidationRestException {
        try {
            Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
            if (!request.getEmail().isOmitted()) {
                return toErasureResponse(
                    erasureService.eraseByEmail(authorization, request.getEmail().getValue(),
                        request.getNote().orElse(null)),
                    timeZone);
            } else if (!request.getPartnerUserId().isOmitted()) {
                return toErasureResponse(
                    erasureService.eraseByPartnerUserId(authorization, request.getPartnerUserId().getValue(),
                        request.getNote().orElse(null)),
                    timeZone);
            } else {
                throw RestExceptionBuilder.newBuilder(ErasureValidationRestException.class)
                    .withErrorCode(ErasureValidationRestException.EMPTY_REQUEST)
                    .build();
            }
        } catch (AuthorizationException e) {
            throw RestExceptionBuilder.newBuilder(UserAuthorizationRestException.class)
                .withErrorCode(UserAuthorizationRestException.ACCESS_DENIED)
                .withCause(e)
                .build();
        } catch (ErasureInvalidEmailAddressException e) {
            throw RestExceptionBuilder.newBuilder(ErasureValidationRestException.class)
                .withErrorCode(ErasureValidationRestException.INVALID_ERASURE_EMAIL)
                .addParameter("email", request.getEmail())
                .withCause(e)
                .build();
        }
    }

    private static ErasureResponse toErasureResponse(Erasure erasure, ZoneId timeZone) {
        return new ErasureResponse(ErasureStatus.valueOf(erasure.getStatus().name()),
            erasure.getNote().orElse(null), erasure.getCreatedDate().atZone(timeZone));
    }
}
