package com.extole.client.rest.schedule;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Hidden
@Path("/v2/schedules")
@Tag(name = "/v2/schedules", description = "Schedule")
public interface ScheduleEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List schedules")
    List<ScheduleResponse> listSchedules(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam ScheduleListRequest request) throws UserAuthorizationRestException;

    @GET
    @Path("/{schedule_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get schedule")
    ScheduleResponse getSchedule(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The id of the schedule to be retrieved",
            required = true) @PathParam("schedule_id") String scheduleId,
        @TimeZoneParam ZoneId timeZone) throws UserAuthorizationRestException, ScheduleRestException;

    @POST
    @Path("/{schedule_id}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cancel schedule")
    ScheduleResponse cancelSchedule(@UserAccessTokenParam String accessToken,
        @Parameter(description = "The id of the schedule to be canceled",
            required = true) @PathParam("schedule_id") String scheduleId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, ScheduleRestException, ScheduleCancelRestException;
}
