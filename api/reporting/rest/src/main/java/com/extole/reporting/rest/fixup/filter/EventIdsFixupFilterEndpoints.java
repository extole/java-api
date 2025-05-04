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

@Path(EventIdsFixupFilterEndpoints.FIXUPS_URI)
public interface EventIdsFixupFilterEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Path("/{fixupId}/filters/event_ids/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    EventIdsFixupFilterResponse getFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException;

    @POST
    @Path("/{fixupId}/filters/event_ids")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    EventIdsFixupFilterResponse createFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        EventIdsFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        EventIdsFixupFilterValidationRestException;

    @PUT
    @Path("/{fixupId}/filters/event_ids/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    EventIdsFixupFilterResponse updateFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId,
        EventIdsFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        EventIdsFixupFilterValidationRestException, FixupFilterUpdateRestException;

    @DELETE
    @Path("/{fixupId}/filters/event_ids/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    EventIdsFixupFilterResponse deleteFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException;
}
