package com.extole.client.rest.person.rewards;

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

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v5/persons/{person_id}/rewards")
@Tag(name = "/v5/persons/{person_id}/rewards", description = "Person rewards")
public interface PersonRewardsEndpoints {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{reward_id}")
    @Operation(summary = "Get a reward by id",
        description = "Returns reward for a person identified by id.")
    PersonRewardResponse get(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @Parameter(description = "Reward id parameter")
        @PathParam("reward_id") String rewardId,
        @TimeZoneParam ZoneId timeZone)
            throws UserAuthorizationRestException, PersonRestException, PersonRewardRestException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a list with rewards",
        description = "Returns rewards for a person, sorted by created date in descending order.")
    List<PersonRewardResponse> list(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @Parameter(description = "The Extole unique profile identifier of this user at Extole.")
        @PathParam("person_id") String personId,
        @BeanParam PersonRewardsListRequest rewardsListRequest,
        @TimeZoneParam ZoneId timeZone)
            throws UserAuthorizationRestException, PersonRestException, PersonRewardsListRestException;
}
