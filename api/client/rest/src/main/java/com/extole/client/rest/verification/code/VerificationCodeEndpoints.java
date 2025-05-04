package com.extole.client.rest.verification.code;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/verification-code")
public interface VerificationCodeEndpoints {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    VerificationCodeResponse create(@UserAccessTokenParam String accessToken) throws UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    VerificationCodeResponse verify(@UserAccessTokenParam String accessToken,
        @QueryParam("verification_code") String code)
        throws UserAuthorizationRestException, VerificationCodeRestException;
}
