package com.extole.reporting.rest.audience.membership;

import java.time.ZoneId;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v5/persons/{person_id}/memberships")
@Tag(name = "/v5/persons/{person_id}/memberships", description = "Person Audience Memberships")
public interface PersonMembershipsEndpoints {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PersonMembershipResponse create(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @RequestBody(required = true) PersonMembershipCreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipValidationRestException,
        PersonMembershipRestException, PersonRestException;

    @DELETE
    @Path("/{audience_id}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonMembershipResponse delete(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("audience_id") String audienceId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipRestException, PersonRestException;
}
