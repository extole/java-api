package com.extole.client.rest.event.stream;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
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

// This endpoint is intended for debugging purposes only
// The pay load of the response exposes implementation details and may change without notice.
@Path("/v6/event-streams")
public interface EventStreamEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    EventStreamResponse create(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        EventStreamCreateRequest request,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{eventStreamId}")
    EventStreamResponse update(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        EventStreamUpdateRequest request, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{eventStreamId}")
    EventStreamResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<EventStreamResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam EventStreamQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/built")
    List<BuiltEventStreamResponse> listBuilt(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam EventStreamQueryParams queryParams,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{eventStreamId}/built")
    BuiltEventStreamResponse getBuilt(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException;

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{eventStreamId}")
    EventStreamResponse archive(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{eventStreamId}/delete")
    EventStreamResponse delete(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{eventStreamId}/unarchive")
    EventStreamResponse unArchive(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") Id<?> eventStreamId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, CampaignComponentValidationRestException,
        EventStreamValidationRestException, EventStreamRestException;

}
