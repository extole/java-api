package com.extole.client.rest.component.sharing.grant;

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

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v1/component-grants")
public interface ComponentGrantEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ComponentGrantResponse> list(@UserAccessTokenParam String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ComponentGrantResponse grant(@UserAccessTokenParam String accessToken, @RequestBody ComponentGrantRequest request,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ComponentGrantRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    ComponentGrantResponse grantAll(@UserAccessTokenParam(requiredScope = Scope.CLIENT_SUPERUSER) String accessToken,
        @RequestBody ComponentGrantAllRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{grantId}")
    ComponentGrantResponse revoke(@UserAccessTokenParam String accessToken, @PathParam("grantId") String grantId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ComponentGrantRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/granters")
    List<ComponentGranterResponse> listGranters(@UserAccessTokenParam String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

}
