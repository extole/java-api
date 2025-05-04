package com.extole.client.topic.rest;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/notifications")
@Tag(name = "/v6/notifications")
public interface UserNotificationEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Operation(summary = "View recent notifications",
        description = "Retrieves notifications - most recent first.")
    List<NotificationResponse> getNotifications(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam NotificationGetRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/read")
    @Operation(summary = "See what notifications have been read")
    NotificationCursorResponse getNotificationCursor(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/read")
    @Operation(summary = "Mark notifications as read by timestamp")
    NotificationCursorResponse updateNotificationCursor(@UserAccessTokenParam String accessToken,
        NotificationCursorRequest notificationCursorRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, NotificationCursorRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{userId}")
    @Operation(summary = "View recent notifications for the specified user",
        description = "Retrieves notifications for the specified user - most recent first.")
    List<NotificationResponse> getNotificationsByUserId(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("userId") String userId, @BeanParam NotificationGetRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{userId}/read")
    @Operation(summary = "See what notifications have been read for the specified user")
    NotificationCursorResponse getNotificationCursorByUserId(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("userId") String userId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{userId}/read")
    @Operation(summary = "Mark notifications as read for the specified user by timestamp")
    NotificationCursorResponse updateNotificationCursorByUserId(@UserAccessTokenParam String accessToken,
        @PathParam("userId") String userId, NotificationCursorRequest notificationCursorRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException, NotificationCursorRestException;
}
