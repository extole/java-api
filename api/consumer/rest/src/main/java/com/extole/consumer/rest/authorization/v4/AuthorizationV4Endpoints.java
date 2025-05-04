package com.extole.consumer.rest.authorization.v4;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.authorization.AuthorizationDurationRestException;
import com.extole.consumer.rest.common.AuthorizationIdentifyRestException;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.common.ReissueTokenRestException;
import com.extole.consumer.rest.response.DropsAccessTokenCookie;

@Path("/v4/token")
@Tag(name = "/v4/token", description = "AuthorizationV4")
@DropsAccessTokenCookie
public interface AuthorizationV4Endpoints {

    @Hidden
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    TokenV4Response createToken(Optional<TokenV4Request> tokenV4Request, @AccessTokenParam String accessToken)
        throws AuthorizationRestException, ReissueTokenRestException, AuthorizationIdentifyRestException,
        AuthorizationDurationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/refresh")
    @DefaultApplicationJSON
    TokenV4Response refreshToken(Optional<TokenV4Request> tokenV4Request, @AccessTokenParam String accessToken)
        throws AuthorizationRestException, ReissueTokenRestException;

    @Operation(summary = "Returns the unique access token associated with the user in the browser.")
    @GET
    @Path("/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    TokenV4Response getTokenDetails(@PathParam("token") String requestedAccessToken) throws AuthorizationRestException;

    @Operation(summary = "Returns the unique access token associated with the user in the browser.",
        description = "Support POST to get token details to allow request without token in url.")
    @POST
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    TokenV4Response getTokenPost(@AccessTokenParam String accessToken) throws AuthorizationRestException;

    /**
     * Returns the access token associated with this user, if the user doesn't have an associated
     * access token an anonymous access token (with minimal scope) is returned.
     *
     * This end point relies on cookies and is intended for use in the context of a web browser.
     */
    @Operation(summary = "By calling the Get Token endpoint a new access token is created.",
        description = "By calling the Get Token endpoint a new access token is created.  " +
            "Extole will attempt to detect who the advocate is (based on cookies, browser fingerprint, etc) " +
            "and provide them an access token with the greatest number of capabilities possible.  If Extole is " +
            "unable to recognize the advocate the access token will only have the UPDATE_PROFILE capability available.")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    TokenV4Response getToken(@AccessTokenParam String accessToken);

    @Deprecated // TODO REMOVE ENG-9666
    @Hidden
    @POST
    @Path("/upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    TokenV4Response upgradeToken(@AccessTokenParam String accessToken, UpgradeTokenV4Request request)
        throws AuthorizationRestException, UpgradeTokenV4RestException;

    @Deprecated // TODO REMOVE ENG-9666
    @Hidden
    @POST
    @Path("/{token}/upgrade")
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    TokenV4Response upgradeToken(@AccessTokenParam String accessToken, @PathParam("token") String requestedAccessToken,
        UpgradeTokenV4Request request) throws AuthorizationRestException, UpgradeTokenV4RestException;

    @Operation(summary = "Deletes the unique access token identified in the request from the associate user's profile.",
        description = "It is optional to pass a token in the URL.  If no token is passed in the URL it " +
            "will attempt to delete any implied token that exists as a cookie, fingerprint, etc.")
    @DELETE
    @Path("/{token}")
    void deleteToken(@AccessTokenParam String accessToken, @PathParam("token") String requestedAccessToken)
        throws AuthorizationRestException;

    @Operation(summary = "Deletes the unique access token identified in the request from the associate user's profile.",
        description = "It is optional to pass a token in the URL.  If no token is passed in the URL it " +
            "will attempt to delete any implied token that exists as a cookie, fingerprint, etc.")
    @DELETE
    void deleteToken(@AccessTokenParam String accessToken) throws AuthorizationRestException;
}
