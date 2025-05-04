package com.extole.client.rest.event.stream;

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

import com.extole.client.rest.campaign.component.CampaignComponentValidationRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;

@Path("/v6/event-streams/{eventStreamId}/filters")
public interface EventStreamFilterEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    EventStreamFilterResponse create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        EventStreamFilterCreateRequest request,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException, EventStreamFilterRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{filterId}")
    EventStreamFilterResponse update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @PathParam("filterId") Id<?> eventStreamFilterId,
        EventStreamFilterUpdateRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException, EventStreamFilterRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{filterId}")
    EventStreamFilterResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @PathParam("filterId") Id<?> eventStreamFilterId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException, EventStreamFilterRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<? extends EventStreamFilterResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, EventStreamRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{filterId}")
    EventStreamFilterResponse archive(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @PathParam("filterId") Id<?> eventStreamFilterId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, EventStreamRestException;
}
