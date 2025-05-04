package com.extole.consumer.rest.me.reward;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.extole.common.rest.authorization.AccessTokenParam;
import com.extole.common.rest.producer.DefaultApplicationJSON;
import com.extole.consumer.rest.common.AuthorizationRestException;

@Path("/v4/me/rewards")
@Tag(name = "/v4/me/rewards", description = "MeReward")
public interface MeRewardEndpoints {

    @Hidden
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    @Produces(MediaType.APPLICATION_JSON)
    @DefaultApplicationJSON
    ClaimRewardResponse claimReward(@AccessTokenParam(readCookie = false) String accessToken,
        ClaimRewardRequest request)
        throws AuthorizationRestException, ClaimRewardRestException;

    @Hidden
    @GET
    @Path("/status/{polling_id}")
    @Produces(MediaType.APPLICATION_JSON)
    ClaimRewardPollingResponse getClaimRewardStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("polling_id") String pollingId) throws AuthorizationRestException;

    @Hidden
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<RewardResponse> getRewards(@AccessTokenParam(readCookie = false) String accessToken)
        throws AuthorizationRestException;

    @Hidden
    @GET
    @Path("/{rewardId}")
    @Produces(MediaType.APPLICATION_JSON)
    RewardResponse getReward(@AccessTokenParam(readCookie = false) String accessToken,
        @PathParam("rewardId") String rewardId)
        throws AuthorizationRestException, RewardRestException;

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Poll this endpoint in order to get Person reward")
    PollingRewardResponse getRewardStatus(@AccessTokenParam(readCookie = false) String accessToken,
        @QueryParam("polling_id") Optional<String> pollingId, @QueryParam("reward_name") Optional<String> rewardName,
        @QueryParam("partner_event_id") Optional<String> partnerEventId) throws AuthorizationRestException,
        RewardRestException;

}
