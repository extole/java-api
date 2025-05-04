package com.extole.client.rest.impl.campaign.incentive;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.incentive.CampaignIncentiveEndpoints;
import com.extole.client.rest.campaign.incentive.CampaignIncentiveRestException;
import com.extole.client.rest.campaign.incentive.IncentiveResponse;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.service.campaign.CampaignNotFoundException;
import com.extole.model.service.campaign.CampaignService;

@Provider
public class CampaignIncentiveEndpointsImpl implements CampaignIncentiveEndpoints {

    private final CampaignService campaignService;
    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignIncentiveRestMapper incentiveRestMapper;

    @Inject
    public CampaignIncentiveEndpointsImpl(CampaignService campaignService,
        ClientAuthorizationProvider authorizationProvider,
        CampaignIncentiveRestMapper incentiveRestMapper) {
        this.authorizationProvider = authorizationProvider;
        this.campaignService = campaignService;
        this.incentiveRestMapper = incentiveRestMapper;
    }

    @Override
    public IncentiveResponse get(String accessToken, String campaignId)
        throws UserAuthorizationRestException, CampaignIncentiveRestException {
        Authorization userAuthorization = authorizationProvider.getClientAuthorization(accessToken);
        try {
            Campaign campaign = campaignService.getPublishedOrDraftAnyStateCampaign(userAuthorization,
                Id.valueOf(campaignId));
            return incentiveRestMapper.toResponse(campaign);
        } catch (CampaignNotFoundException e) {
            throw RestExceptionBuilder.newBuilder(CampaignIncentiveRestException.class)
                .withErrorCode(CampaignIncentiveRestException.INVALID_CAMPAIGN_ID)
                .addParameter("campaign_id", campaignId)
                .withCause(e).build();
        }
    }
}
