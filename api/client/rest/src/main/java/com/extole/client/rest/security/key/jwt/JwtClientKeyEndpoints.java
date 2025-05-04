package com.extole.client.rest.security.key.jwt;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.extole.client.rest.security.key.ClientKeyRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO use ClientKeyEndpoints ENG-14893
@Path("/v2/settings/security/keys/jwt")
public interface JwtClientKeyEndpoints {

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    @Path("/{key_id}/verify-jwt")
    JwtClientKeyVerifyJwtResponse verifyJwt(@UserAccessTokenParam String accessToken,
        @PathParam("key_id") String keyId, JwtClientKeyVerifyJwtRequest request)
        throws UserAuthorizationRestException, ClientKeyRestException, JwtClientKeyJwtVerifyRestException;

}
