package com.extole.client.rest.targeting.settings;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/settings/targeting")
@Tag(name = "targeting", description = "Targeting Settings Management")
public interface TargetingSettingsEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get current targeting settings for the client")
    TargetingSettingsResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException, TargetingSettingsRestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update targeting settings for the client")
    TargetingSettingsResponse update(
        @UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        TargetingSettingsUpdateRequest request)
        throws UserAuthorizationRestException, TargetingSettingsRestException;
}
