package com.extole.client.topic.rest;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
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

@Path("/v6/notifications/local")
@Tag(name = "/v6/notifications/local")
public interface UserNotificationLocalEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{userId}")
    @Operation(summary = "View recent notifications for the specified user",
        description = "Retrieves notifications for the specified user - most recent first.")
    List<NotificationResponse> getNotifications(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("userId") String userId, @BeanParam NotificationGetRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, UserNotificationRestException;

}
