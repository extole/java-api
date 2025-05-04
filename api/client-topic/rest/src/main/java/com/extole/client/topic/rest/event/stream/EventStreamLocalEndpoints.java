package com.extole.client.topic.rest.event.stream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.event.stream.EventStreamEventFilterQueryParams;
import com.extole.client.rest.event.stream.EventStreamEventResponse;
import com.extole.client.rest.event.stream.EventStreamRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v6/event-streams/local")
@Tag(name = "/v6/event-streams/local")
public interface EventStreamLocalEndpoints {

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @POST
    @Path("/{eventStreamId}/recent")
    @Operation(summary = "View recent event streams for the pod",
        description = "Retrieves event streams - most recent first - from the pod requested. Best used with"
            + " api-X.extole.io rather than api.extole.io. For general purposes please use the non-local endpoint"
            + " api.extole.io/v6/event-streams/{eventStreamId}/recent")
    List<EventStreamEventResponse> getEvents(@UserAccessTokenParam String accessToken,
        @PathParam("eventStreamId") String eventStreamId,
        @Nullable @BeanParam EventStreamEventFilterQueryParams filterRequest,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, EventStreamRestException;

}
