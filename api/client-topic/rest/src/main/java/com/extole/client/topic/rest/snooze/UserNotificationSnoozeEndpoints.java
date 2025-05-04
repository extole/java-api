package com.extole.client.topic.rest.snooze;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/notifications")
public interface UserNotificationSnoozeEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Operation(summary = "View user snoozes",
        description = "Retrieves snoozes - most recent first.")
    @Path("/{userId}/snoozes")
    List<SnoozeResponse> getSnoozesByUserId(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("userId") String userId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Operation(summary = "View user snoozes",
        description = "Retrieves snoozes - most recent first.")
    @Path("/snoozes")
    List<SnoozeResponse> getSnoozes(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, SnoozeRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Operation(summary = "Snooze notifications for a set of tags")
    @Path("/{userId}/snoozes")
    SnoozeResponse createSnoozeByUserId(@UserAccessTokenParam String accessToken, @PathParam("userId") String userId,
        SnoozeRequest snoozeRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeValidationRestException, SnoozeRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Operation(summary = "Snooze notifications for a set of tags")
    @Path("/snoozes")
    SnoozeResponse createSnooze(@UserAccessTokenParam String accessToken, SnoozeRequest snoozeRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeValidationRestException, SnoozeRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{userId}/snoozes/{snoozeId}")
    @Operation(summary = "Retrieve a specific snooze")
    SnoozeResponse getSnoozeByUserId(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("userId") String userId, @PathParam("snoozeId") String snoozeId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/snoozes/{snoozeId}")
    @Operation(summary = "Retrieve a specific snooze")
    SnoozeResponse getSnooze(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("snoozeId") String snoozeId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{userId}/snoozes/{snoozeId}")
    @Operation(summary = "Deletes a specific snooze")
    SnoozeResponse deleteSnoozeByUserId(@UserAccessTokenParam String accessToken, @PathParam("userId") String userId,
        @PathParam("snoozeId") String snoozeId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, SnoozeRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/snoozes/{snoozeId}")
    @Operation(summary = "Deletes a specific snooze")
    SnoozeResponse deleteSnooze(@UserAccessTokenParam String accessToken, @PathParam("snoozeId") String snoozeId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, SnoozeRestException;
}
