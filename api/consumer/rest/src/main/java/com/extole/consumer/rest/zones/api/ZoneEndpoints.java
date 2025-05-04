package com.extole.consumer.rest.zones.api;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

// See LinkFollowingEndpoint for how the /zone request is handled
@Path("/v6/zones")
@Tag(name = "/v6/zones", description = "Zone")
public interface ZoneEndpoints {

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Operation(description = "This endpoint is REST - optimized. For web behavior consider /zones endpoints.")
    ZoneResponse render(@AccessTokenParam(readCookie = false) String accessToken,
        RenderZoneRequest renderZoneRequest)
        throws AuthorizationRestException, RenderZoneEventRestException;

    // TODO request should be strongly typed - ENG-15275
    @POST
    @Path("/{event_name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    @Operation(description = "This endpoint is REST - optimized. For web behavior consider /zones endpoints.")
    ZoneResponse render(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("event_name") String eventName, Optional<UnnamedRenderZoneRequest> renderZoneRequest)
        throws AuthorizationRestException, RenderZoneEventRestException;
}
