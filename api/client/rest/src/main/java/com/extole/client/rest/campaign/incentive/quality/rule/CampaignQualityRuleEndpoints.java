package com.extole.client.rest.campaign.incentive.quality.rule;

import java.util.List;

import javax.ws.rs.Consumes;
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
import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaign_id}{version:(/version/.+)?}/incentive/quality-rules")
public interface CampaignQualityRuleEndpoints {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    List<QualityRuleResponse> list(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId)
        throws UserAuthorizationRestException, CampaignIncentiveRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{quality_rule_id}")
    QualityRuleResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("quality_rule_id") String qualityRuleId)
        throws UserAuthorizationRestException, QualityRuleRestException, CampaignIncentiveRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("/{quality_rule_id}")
    QualityRuleResponse update(@UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion,
        @PathParam("quality_rule_id") String qualityRuleId,
        QualityRuleRequest request)
        throws UserAuthorizationRestException, CampaignRestException, QualityRuleRestException,
        QualityRuleValidationRestException, CampaignIncentiveRestException, BuildCampaignRestException,
        CampaignUpdateRestException;

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/disable")
    List<QualityRuleResponse> disableAllQualityRules(
        @UserAccessTokenParam String accessToken,
        @PathParam("campaign_id") String campaignId,
        @PathParam("version") String expectedCurrentVersion)
        throws UserAuthorizationRestException, CampaignRestException, CampaignIncentiveRestException,
        BuildCampaignRestException, CampaignUpdateRestException;

}
