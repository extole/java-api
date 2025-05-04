package com.extole.client.rest.user;

import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Parameter;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.model.SuccessResponse;

@Path("/v2/users")
public interface UserEndpoints {
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    UserResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("userId") String userId) throws UserAuthorizationRestException, UserRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<UserResponse> getAll(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "accepts values: 'account'|'all'")
        @Nullable @QueryParam("scope") @DefaultValue("account") String userAccountScope)
        throws UserAuthorizationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserResponse create(@UserAccessTokenParam String accessToken, UserCreationRequest request)
        throws UserAuthorizationRestException, UserCreateRestException, UserValidationRestException;

    @POST
    @Path("/resend-invite-email")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse resendInviteEmail(ResendInviteEmailRequest request) throws UserAuthorizationRestException;

    @PUT
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserResponse update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("userId") String userId, UserUpdateRequest request)
        throws UserAuthorizationRestException, UserRestException, UserUpdateRestException, UserValidationRestException;

    @PUT
    @Path("/update-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserResponse updatePassword(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        UserPasswordUpdateRequest request)
        throws UserAuthorizationRestException, UserRestException, UserUpdateRestException, UserValidationRestException;

    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse delete(@UserAccessTokenParam String accessToken, @PathParam("userId") String userId)
        throws UserAuthorizationRestException, UserRestException;

    @POST
    @Path("/{userId}/unlock")
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse unlock(@UserAccessTokenParam String accessToken, @PathParam("userId") String userId)
        throws UserAuthorizationRestException, UserRestException;

    @POST
    @Path("/password-reset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SuccessResponse resetPassword(PasswordResetRequest request) throws UserPasswordResetRestException;
}
