package com.extole.client.topic.rest;

import java.time.ZoneId;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/client-events")
@Tag(name = "/v6/client-events")
public interface ClientEventEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Operation(summary = "Creates a clientEvent",
        description = "Useful for triggering notifications to subscriptions matching the event tags/level")
    ClientEventResponse createClientEvent(@UserAccessTokenParam String accessToken,
        ClientEventRequest clientEventRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientEventRestException;

}
