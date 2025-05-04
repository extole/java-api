package com.extole.consumer.rest.authorization;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.model.SuccessResponse;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationIdentifyRestException;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v5/token")
@Tag(name = "/v5/token", description = "Authorization")
public interface AuthorizationEndpoints {

    @Operation(summary = "Get access token details",
        description = "It is optional to pass a token in the URL. If no token is passed in the URL it " +
            "will attempt to return details about any implied token from authorization header.")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    TokenResponse getTokenDetails(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @Operation(summary = "Creates a new access token", description = "It is optional to pass an email or JWT," +
        " email attribute being applicable only if client identity key is \"email\"")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    TokenResponse createToken(Optional<CreateTokenRequest> createTokenRequest)
        throws AuthorizationVerificationJwtRestException, AuthorizationIdentifyRestException,
        AuthorizationDurationRestException;

    @Operation(summary = "Deletes the unique access token identified in the request from the associate user's profile.",
        description = "It is optional to pass a token in the URL. If no token is passed in the URL it " +
            "will attempt to delete any implied token from authorization header.")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse deleteToken(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;
}
