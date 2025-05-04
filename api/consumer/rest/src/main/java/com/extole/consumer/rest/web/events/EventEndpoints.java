package com.extole.consumer.rest.web.events;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.response.DropsAccessTokenCookie;

@Path("/events")
@Tag(name = "/events", description = "Event")
@DropsAccessTokenCookie
public interface EventEndpoints {

    @Operation(summary = "Submits the event with the name specified in URI.",
        description = "Event data may be passed as query parameters and/or JWT encoded query parameter." +
            " This endpoint may return a new access_token cookie." +
            " Please use the more standard /api/vN/events endpoint if you want to manage the access token.")
    @ApiResponses({
        @ApiResponse(description = "Always return 200. Strongly typed response body, see SubmitEventResponse. ",
            headers = {
                @Header(name = "X-Extole-Token"),
                @Header(name = "X-Extole-Cookie-Consent")
            },
            content = @Content(schema = @Schema(implementation = SubmitEventResponse.class)))
    })
    @GET
    @Path("/{event_name}")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    SubmitEventResponse fetch(@AccessTokenParam String accessToken, @PathParam("event_name") String eventName);

    // TODO request should not be strongly typed - ENG-15275
    @Operation(summary = "Submits the event with the name specified in request body.",
        description = "Event data may be passed as query parameters/request body and/or " +
            " JWT encoded query parameter/request body attribute." +
            " This endpoint may return a new access_token cookie." +
            " Please use the more standard /api/vN/events endpoint if you want to manage the access token.")
    @ApiResponses({
        @ApiResponse(description = "Always return 200. Strongly typed response body, see SubmitEventResponse. ",
            headers = {
                @Header(name = "X-Extole-Token"),
                @Header(name = "X-Extole-Cookie-Consent")
            },
            content = @Content(schema = @Schema(implementation = SubmitEventResponse.class)))
    })
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SubmitEventResponse post(@AccessTokenParam String accessToken, SubmitEventRequest submitEventRequest);

    @Hidden // stub generation doesn't work with FormData and Body data on the same method
    @POST
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    SubmitEventResponse postFormEncoded(@AccessTokenParam String accessToken,
        SubmitEventRequest submitEventRequest);

    @GET
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    SubmitEventResponse fetch(@AccessTokenParam String accessToken);

}
