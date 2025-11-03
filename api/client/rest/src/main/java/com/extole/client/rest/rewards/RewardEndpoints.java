package com.extole.client.rest.rewards;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import com.extole.client.rest.person.PersonRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/rewards")
public interface RewardEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<RewardResponse> listRewards(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @BeanParam RewardListRequest rewardListRequest)
        throws UserAuthorizationRestException, RewardQueryRestException, QueryLimitsRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/state_summary")
    List<RewardStateSummaryResponse> stateSummary(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @QueryParam("period") Optional<Period> period,
        @QueryParam("period_count") Optional<String> periodCount,
        @QueryParam("end_date") Optional<ZonedDateTime> endDate,
        @QueryParam("success_only") Optional<Boolean> successOnly,
        @QueryParam("reward_supplier_id") Optional<String> multipleRewardSupplierIds,
        @QueryParam("reward_type") Optional<String> multipleRewardTypes,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}")
    RewardResponse getReward(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException; // TODO ENG-10518

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}/history")
    List<RewardStateResponse> getRewardStateHistory(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}/fulfillments")
    List<FulfilledRewardStateResponse> getRewardFulfillments(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}/sends")
    List<SentRewardStateResponse> getRewardSends(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}/redeems")
    List<RedeemedRewardStateResponse> getRewardRedeems(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}/fails")
    List<FailedRewardStateResponse> getRewardFails(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}/cancels")
    List<CanceledRewardStateResponse> getRewardCancels(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_id}/revokes")
    List<RevokedRewardStateResponse> getRewardRevokes(
        @UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/retry")
    RewardResponse retryReward(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/cancel")
    RewardResponse cancel(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @RequestBody Optional<CancelRewardRequest> cancelRewardRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/revoke")
    RewardResponse revoke(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("reward_id") String rewardId, @RequestBody Optional<RevokeRewardRequest> revokeRewardRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{reward_id}")
    RewardResponse updateReward(@UserAccessTokenParam String accessToken,
        @PathParam("reward_id") String rewardId, @RequestBody RewardUpdateRequest rewardUpdateRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, PersonRestException;

}
