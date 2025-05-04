package com.extole.client.zone.rest;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v5/zones")
public interface ClientZonesEndpoints {

    @GET
    @Path("/{zone_name}")
    @Consumes(MediaType.WILDCARD)
    Response fetch(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("zone_name") String zoneName)
        throws UserAuthorizationRestException, ClientZoneRestException;

    @GET
    @Consumes(MediaType.WILDCARD)
    Response fetch(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException, ClientZoneRestException;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED,
        MediaType.MULTIPART_FORM_DATA})
    Response post(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        Optional<ClientRenderZoneRequest> request) throws UserAuthorizationRestException, ClientZoneRestException;

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED,
        MediaType.MULTIPART_FORM_DATA})
    @Path("/{zone_name}")
    Response post(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("zone_name") String zoneName, Optional<UnnamedClientRenderZoneRequest> request)
        throws UserAuthorizationRestException, ClientZoneRestException;

}
