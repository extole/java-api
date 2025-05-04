package com.extole.client.rest.person.v2;

import java.time.ZoneId;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.person.PersonRestException;
import com.extole.client.rest.person.RuntimePersonEndpoints;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

/**
 * @deprecated Use {@link RuntimePersonEndpoints} for reading data.
 *             Create/update operations exist just in deprecated /v2/persons/journeys
 */
@Deprecated // TODO remove in ENG-13035
@Path("/v2/persons/{person_id}/journeys")
public interface PersonJourneyV2Endpoints {

    // TODO Rename type to journey_name - ENG-18547
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PersonJourneyV2Response> getJourneys(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @Nullable @QueryParam("container") String container,
        @Nullable @QueryParam("type") String journeyName,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{journey_id}")
    PersonJourneyV2Response getJourney(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("journey_id") String journeyId,
        @TimeZoneParam ZoneId timeZone)
        throws PersonRestException, UserAuthorizationRestException, PersonJourneyV2ValidationRestException;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    PersonJourneyV2Response createJourney(@UserAccessTokenParam String accessToken,
        @PathParam("person_id") String personId,
        PersonJourneyV2CreateRequest journeyRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonJourneyV2ValidationRestException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{journey_id}")
    PersonJourneyV2Response updateJourney(@UserAccessTokenParam String accessToken,
        @PathParam("person_id") String personId,
        @PathParam("journey_id") String journeyId,
        PersonJourneyV2UpdateRequest journeyRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, PersonRestException, PersonJourneyV2ValidationRestException;
}
