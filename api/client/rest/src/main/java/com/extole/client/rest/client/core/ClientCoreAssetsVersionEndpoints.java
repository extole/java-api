package com.extole.client.rest.client.core;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/core-assets-version")
public interface ClientCoreAssetsVersionEndpoints {

    @POST
    @Path("/{clientId}")
    @DefaultApplicationJSON
    @Produces(MediaType.APPLICATION_JSON)
    ClientCoreAssetsVersionResponse incrementClientCoreAssetsVersion(@UserAccessTokenParam String accessToken,
        @Deprecated // TODO clientId != authorization unsupported. ENG-13367
        @PathParam("clientId") String clientId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/list")
    @DefaultApplicationJSON
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientCoreAssetsVersionResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @DefaultApplicationJSON
    @Produces(MediaType.APPLICATION_JSON)
    ClientCoreAssetsVersionResponse getLatestCoreAssetsVersion(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Path("/{coreAssetsVersion}")
    @DefaultApplicationJSON
    @Produces(MediaType.APPLICATION_JSON)
    ClientCoreAssetsVersionResponse getCoreAssetsVersion(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("coreAssetsVersion") Long coreAssetsVersion, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ClientCoreAssetsVersionRestException;

}
