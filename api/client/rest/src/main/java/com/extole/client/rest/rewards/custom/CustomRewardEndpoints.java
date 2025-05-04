package com.extole.client.rest.rewards.custom;

import java.time.ZoneId;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.rewards.RewardQueryRestException;
import com.extole.client.rest.rewards.RewardResponse;
import com.extole.client.rest.rewards.RewardRestException;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.time.TimeZoneParam;

@Path("/v2/rewards/custom-reward/")
public interface CustomRewardEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/fulfilled")
    RewardResponse fulfilled(@UserAccessTokenParam String accessToken, @PathParam("reward_id") String rewardId,
        FulfilledRewardRequest fulfilledRewardRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/sent")
    RewardResponse sent(@UserAccessTokenParam String accessToken, @PathParam("reward_id") String rewardId,
        SentRewardRequest sentRewardRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/fulfilled_and_sent")
    RewardResponse fulfilledAndSent(@UserAccessTokenParam String accessToken,
        @PathParam("reward_id") String rewardId,
        FulfilledAndSentRewardRequest fulfilledAndSentRewardRequest,
        @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/redeemed")
    RewardResponse redeemed(@UserAccessTokenParam String accessToken, @PathParam("reward_id") String rewardId,
        RedeemedRewardRequest redeemedRewardRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("/{reward_id}/failed")
    RewardResponse failed(@UserAccessTokenParam String accessToken, @PathParam("reward_id") String rewardId,
        FailedRewardRequest failedRewardRequest, @TimeZoneParam ZoneId timeZone)
        throws UserAuthorizationRestException, RewardRestException, RewardQueryRestException;

}
