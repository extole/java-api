package com.extole.reporting.rest.audience.stats;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.api.audience.Audience;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.id.Id;
import com.extole.reporting.rest.audience.member.AudienceMemberRestException;

@Path("/v1/audiences/{audience_id}/stats")
@Tag(name = "/v1/audiences/{audience_id}/stats")
public interface AudienceStatsEndpoints {

    @GET
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Get membership statistics for an audience")
    AudienceStatsResponse getStats(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("audience_id") Id<Audience> audienceId)
        throws UserAuthorizationRestException, AudienceMemberRestException;

}
