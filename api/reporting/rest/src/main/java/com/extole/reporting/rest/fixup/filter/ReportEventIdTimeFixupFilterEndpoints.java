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

@Path(ReportEventIdTimeFixupFilterEndpoints.FIXUPS_URI)
public interface ReportEventIdTimeFixupFilterEndpoints {

    String FIXUPS_URI = "/v2/fixups/{fixupId}/filters/report_event_id_time";

    @GET
    @Path("/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportEventIdTimeFixupFilterResponse getFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId, @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ReportEventIdTimeFixupFilterResponse createFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId, ReportEventIdTimeFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        ReportEventIdTimeFixupFilterValidationRestException;

    @PUT
    @Path("/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ReportEventIdTimeFixupFilterResponse updateFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId, @PathParam("filterId") String filterId,
        ReportEventIdTimeFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        ReportEventIdTimeFixupFilterValidationRestException, FixupFilterUpdateRestException;

    @DELETE
    @Path("/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportEventIdTimeFixupFilterResponse deleteFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId, @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException;
}
