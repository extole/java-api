package com.extole.client.topic.rest.event.stream;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.event.stream.EventStreamEventFilterQueryParams;
import com.extole.client.rest.event.stream.EventStreamEventResponse;
import com.extole.client.rest.event.stream.EventStreamRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/event-streams")
public interface EventStreamEventEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{eventStreamId}/events")
    List<EventStreamEventResponse> events(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("eventStreamId") String eventStreamId,
        @BeanParam EventStreamEventFilterQueryParams filterRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, EventStreamRestException;
}
