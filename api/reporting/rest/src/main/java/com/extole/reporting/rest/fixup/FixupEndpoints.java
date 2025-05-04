package com.extole.reporting.rest.fixup;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path(FixupEndpoints.FIXUPS_URI)
public interface FixupEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<FixupResponse> listFixups(@UserAccessTokenParam String accessToken,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @GET
    @Path("/{fixupId}")
    @Produces(MediaType.APPLICATION_JSON)
    FixupResponse getFixup(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    FixupResponse createFixup(@UserAccessTokenParam String accessToken,
        FixupRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FixupValidationRestException;

    @PUT
    @Path("/{fixupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    FixupResponse updateFixup(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        FixupRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException, FixupUpdateRestException;

    @DELETE
    @Path("/{fixupId}")
    @Produces(MediaType.APPLICATION_JSON)
    FixupResponse deleteFixup(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException, FixupUpdateRestException;
}
