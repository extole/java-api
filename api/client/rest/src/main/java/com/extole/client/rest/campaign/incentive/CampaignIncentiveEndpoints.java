package com.extole.client.rest.campaign.incentive;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.extole.common.rest.authorization.Scope;
import com.extole.common.rest.authorization.UserAccessTokenParam;
import com.extole.common.rest.exception.UserAuthorizationRestException;

@Path("/v2/campaigns/{campaign_id}/incentive")
public interface CampaignIncentiveEndpoints {
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    IncentiveResponse get(@UserAccessTokenParam(requiredScope = Scope.USER_SUPPORT) String accessToken,
        @PathParam("campaign_id") String campaignId)
        throws UserAuthorizationRestException, CampaignIncentiveRestException;
}
