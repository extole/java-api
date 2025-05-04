package com.extole.reporting.rest.fixup;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path(FixupExecutionEndpoints.FIXUPS_URI)
public interface FixupExecutionEndpoints {

    String FIXUPS_URI = "/v2/fixups/{fixupId}/executions";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<FixupExecutionResponse> listExecutions(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException;

    @GET
    @Path("/{executionId}")
    @Produces(MediaType.APPLICATION_JSON)
    FixupExecutionResponse getExecution(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @PathParam("executionId") String executionId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException,
        FixupExecutionRestException;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    FixupExecutionResponse start(@UserAccessTokenParam String accessToken,
        @PathParam("fixupId") String fixupId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, FixupRestException,
        FixupExecutionInProgressRestException, FixupExecutionRestException, FixupExecutedRestException;
}
