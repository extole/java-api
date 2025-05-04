package com.extole.client.rest.logo;

import java.time.ZoneId;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.request.FileInputStreamRequest;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/logo")
public interface LogoEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    LogoResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, LogoRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    LogoResponse create(@UserAccessTokenParam String accessToken, FileInputStreamRequest request,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, LogoValidationRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    LogoResponse archive(@UserAccessTokenParam String accessToken, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, LogoRestException;

    @GET
    @Path("/image")
    @Produces("image/png")
    Response downloadLogoImage(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken)
        throws UserAuthorizationRestException;
}
