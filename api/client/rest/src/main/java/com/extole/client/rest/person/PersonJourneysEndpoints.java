package com.extole.client.rest.person;

import java.time.ZoneId;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v5/persons/{person_id}/journeys")
@Tag(name = "/v5/persons/{person_id}/journeys", description = "PersonJourney")
public interface PersonJourneysEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with journeys", description = "Returns journeys for a person.")
    List<PersonJourneyResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @BeanParam PersonJourneysListRequest listRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonJourneysListRestException;

    @GET
    @Path("/{journey_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a journey", description = "Returns journey for a person and id.")
    PersonJourneyResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Journey id parameter.")
        @PathParam("journey_id") String journeyId,
        @TimeZoneParam ZoneId timeZone)
        throws PersonRestException, UserAuthorizationRestException, PersonJourneyRestException;
}
