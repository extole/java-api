package com.extole.consumer.rest.web.zones;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.consumer.rest.common.AuthorizationRestException;
import com.extole.consumer.rest.response.DropsAccessTokenCookie;

@Path("/zones")
@DropsAccessTokenCookie
public interface ZonesEndpoints {

    /**
     * Zone endpoint will serve creative or HTTP 302 to landing page or return HTTP 200.
     *
     * In case of an error
     * - an empty HTTP 200 will be returned
     * - X_EXTOLE_ERROR header will be set to a JSON representation of ZoneRequestException
     */
    @GET
    @Path("/{zone_name}")
    @Consumes(MediaType.WILDCARD)
    Response fetch(@AccessTokenParam String accessToken, @PathParam("zone_name") String zoneName)
        throws AuthorizationRestException;

    @GET
    @Consumes(MediaType.WILDCARD)
    Response fetch(@AccessTokenParam String accessToken) throws AuthorizationRestException;

    // TODO request should not be strongly typed - ENG-15275
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED,
        MediaType.MULTIPART_FORM_DATA})
    @Operation(description = "This endpoint is optimized for calling from a web page." +
        "For proper RESTful endpoint consider /v6/zones.")
    Response post(@AccessTokenParam String accessToken, Optional<RenderZoneRequest> renderZoneRequest)
        throws AuthorizationRestException;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED,
        MediaType.MULTIPART_FORM_DATA})
    @Path("/{zone_name}")
    @Operation(description = "This endpoint is optimized for calling from a web page." +
        "For proper RESTful endpoint consider /v6/zones.")
    Response post(@AccessTokenParam String accessToken, @PathParam("zone_name") String zoneName,
        Optional<UnnamedRenderZoneRequest> request) throws AuthorizationRestException;
}
