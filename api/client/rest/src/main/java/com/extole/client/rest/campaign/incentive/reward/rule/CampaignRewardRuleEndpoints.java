package com.extole.client.rest.campaign.incentive.reward.rule;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.CampaignUpdateRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaign_id}{version:(/version/.+)?}/incentive/reward-rules")
public interface CampaignRewardRuleEndpoints {
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<RewardRuleResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId)
        throws UserAuthorizationRestException, CampaignRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{reward_rule_id}")
    RewardRuleResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("reward_rule_id") String rewardRuleId)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    RewardRuleResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        RewardRuleCreateRequest rewardRule)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleValidationRestException,
        RewardSupplierRestException, RewardRuleRestException, BuildCampaignRestException, CampaignUpdateRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{reward_rule_id}")
    RewardRuleResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("reward_rule_id") String rewardRuleId,
        RewardRuleUpdateRequest rewardRule)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleRestException,
        RewardRuleValidationRestException, RewardSupplierRestException, BuildCampaignRestException,
        CampaignUpdateRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{reward_rule_id}")
    RewardRuleResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("reward_rule_id") String rewardRuleId)
        throws UserAuthorizationRestException, CampaignRestException, RewardRuleRestException,
        BuildCampaignRestException, CampaignUpdateRestException;
}
