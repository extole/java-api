package com.extole.client.rest.campaign.incentive.transition.rule;

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
import com.extole.client.rest.campaign.incentive.CampaignIncentiveRestException;
import com.extole.client.rest.reward.supplier.RewardSupplierRestException;
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaign_id}{version:(/version/.+)?}/incentive/transition-rules")
public interface CampaignTransitionRuleEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<TransitionRuleResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId) throws UserAuthorizationRestException,
        CampaignIncentiveRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{transition_rule_id}")
    TransitionRuleResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("transition_rule_id") String transitionRuleId)
        throws UserAuthorizationRestException, CampaignIncentiveRestException, TransitionRuleRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    TransitionRuleResponse create(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        TransitionRuleCreationRequest transitionRule)
        throws UserAuthorizationRestException, CampaignRestException, CampaignIncentiveRestException,
        RewardSupplierRestException, TransitionRuleRestException, TransitionRuleCreationValidationRestException,
        TransitionRuleValidationRestException, BuildCampaignRestException, CampaignUpdateRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{transition_rule_id}")
    TransitionRuleResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("transition_rule_id") String transitionRuleId,
        TransitionRuleRequest transitionRule)
        throws UserAuthorizationRestException, CampaignRestException, CampaignIncentiveRestException,
        RewardSupplierRestException, TransitionRuleRestException, TransitionRuleValidationRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{transition_rule_id}")
    TransitionRuleResponse delete(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("transition_rule_id") String transitionRuleId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignIncentiveRestException,
        TransitionRuleRestException, BuildCampaignRestException, CampaignUpdateRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/delete-all")
    void deleteAllTransitionRules(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion)
        throws UserAuthorizationRestException, CampaignRestException, CampaignIncentiveRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

}
