package com.extole.client.rest.events;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Deprecated // TODO use /v5/events instead and default approve/decline endpoints ENG-10815
@Path("/")
public interface ApprovalEndpoints {

    @GET
    @Path("/v2/events/approve")
    @Produces(MediaType.APPLICATION_JSON)
    Response v2Approve(@UserAccessTokenParam String accessToken,
        @QueryParam("event_id") Long eventId, @QueryParam("partner_conversion_id") String partnerConversionId,
        @QueryParam("event_status") String eventStatus, @QueryParam("note") String note,
        @DefaultValue("false") @QueryParam("force") boolean force)
        throws UserAuthorizationRestException;

    @GET
    @Path("/v3/events/approve")
    @Produces(MediaType.APPLICATION_JSON)
    Response v3Approve(@UserAccessTokenParam String accessToken,
        @QueryParam("event_id") Long eventId, @QueryParam("partner_conversion_id") String partnerConversionId,
        @QueryParam("event_status") String eventStatus, @QueryParam("note") String note,
        @DefaultValue("false") @QueryParam("force") boolean force)
        throws UserAuthorizationRestException;

}
