package com.extole.client.rest.person.v4;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v4/runtime-persons/{person_id}/journeys")
@Tag(name = "/v4/runtime-persons/{person_id}/journeys", description = "RuntimePersonJourney")
public interface RuntimePersonJourneyV4Endpoints {

    // TODO Rename type to journey_name - ENG-18547
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with journeys", description = "Returns journeys for a person.")
    List<PersonJourneyV4Response> getJourneys(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @Parameter(description = "Optional container filter, defaults to production container. " +
            "Pass \"*\" to include steps for all containers") @Nullable @QueryParam("container") String container,
        @Parameter(
            description = "Optional journey type filter, one of friend or advocate.") @Nullable @QueryParam("type") String journeyName,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Path("/{journey_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a journey", description = "Returns journey for a person and id.")
    PersonJourneyV4Response getJourney(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(
            description = "The Extole unique profile identifier of this user at Extole.") @PathParam("person_id") String personId,
        @Parameter(description = "Journey id parameter.") @PathParam("journey_id") String journeyId,
        @TimeZoneParam ZoneId timeZone)
        throws PersonRestException, UserAuthorizationRestException, PersonJourneyRestV4Exception;
}
