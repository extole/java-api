package com.extole.reporting.rest.fixup.filter;

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
import com.extole.reporting.rest.fixup.FixupRestException;

@Path(ProfileIdsFixupFilterEndpoints.FIXUPS_URI)
public interface ProfileIdsFixupFilterEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Path("/{fixupId}/filters/profile_ids/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    ProfileIdsFixupFilterResponse getFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException;

    @POST
    @Path("/{fixupId}/filters/profile_ids")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ProfileIdsFixupFilterResponse createFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        ProfileIdsFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        ProfileIdsFixupFilterValidationRestException;

    @PUT
    @Path("/{fixupId}/filters/profile_ids/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ProfileIdsFixupFilterResponse updateFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId,
        ProfileIdsFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        ProfileIdsFixupFilterValidationRestException, FixupFilterUpdateRestException;

    @DELETE
    @Path("/{fixupId}/filters/profile_ids/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    ProfileIdsFixupFilterResponse deleteFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException;
}
