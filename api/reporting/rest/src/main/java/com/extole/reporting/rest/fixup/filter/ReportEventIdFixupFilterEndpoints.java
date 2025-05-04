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

@Path(ReportEventIdFixupFilterEndpoints.FIXUPS_URI)
public interface ReportEventIdFixupFilterEndpoints {

    String FIXUPS_URI = "/v2/fixups";

    @GET
    @Path("/{fixupId}/filters/report_event_id/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportEventIdFixupFilterResponse getFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException;

    @POST
    @Path("/{fixupId}/filters/report_event_id")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ReportEventIdFixupFilterResponse createFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        ReportEventIdFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterValidationRestException,
        ReportIdFixupFilterValidationRestException;

    @PUT
    @Path("/{fixupId}/filters/report_event_id/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ReportEventIdFixupFilterResponse updateFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId,
        ReportEventIdFixupFilterRequest request)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        ReportIdFixupFilterValidationRestException, FixupFilterUpdateRestException;

    @DELETE
    @Path("/{fixupId}/filters/report_event_id/{filterId}")
    @Produces(MediaType.APPLICATION_JSON)
    ReportEventIdFixupFilterResponse deleteFilter(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("filterId") String filterId)
        throws UserAuthorizationRestException, FixupRestException, FixupFilterRestException,
        FixupFilterUpdateRestException;
}
