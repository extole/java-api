package com.extole.reporting.rest.audience.membership;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.api.audience.Audience;
import com.extole.api.person.Person;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;
import com.extole.id.Id;
import com.extole.reporting.rest.audience.membership.v4.PersonAudienceMembershipV4CreateRequest;
import com.extole.reporting.rest.audience.membership.v4.PersonAudienceMembershipV4Response;

@Path("/v4/runtime-persons/{person_id}/audience-memberships")
@Tag(name = "/v4/runtime-persons", description = "RuntimePersonAudienceMembership")
public interface RuntimePersonAudienceMembershipEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List audience memberships")
    List<PersonAudienceMembershipV4Response> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") Id<Person> personId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonMembershipRestException;

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Operation(summary = "Create audience membership")
    PersonAudienceMembershipV4Response create(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") Id<Person> personId,
        @RequestBody(required = true) PersonAudienceMembershipV4CreateRequest createRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipValidationRestException,
            PersonMembershipRestException, PersonRestException;

    @DELETE
    @Path("/{audience_id}")
    @Produces(APPLICATION_JSON)
    @Operation(summary = "Delete audience membership")
    PersonAudienceMembershipV4Response delete(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") Id<Person> personId,
        @PathParam("audience_id") Id<Audience> audienceId,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonMembershipRestException, PersonRestException;

}
