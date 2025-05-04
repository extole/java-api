package com.extole.client.rest.impl.campaign.controller.action;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionEndpoints;
import com.extole.client.rest.campaign.controller.action.CampaignControllerActionResponse;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.ActionableCampaignStep;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerAction;

@Provider
public class CampaignControllerActionEndpointsImpl implements CampaignControllerActionEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignControllerActionResponseMapperRepository actionMapperRepository;
    private final CampaignProvider campaignProvider;

    @Inject
    public CampaignControllerActionEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignControllerActionResponseMapperRepository actionMapperRepository,
        CampaignProvider campaignProvider) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.actionMapperRepository = actionMapperRepository;
        this.campaignProvider = campaignProvider;
    }

    @Override
    public List<CampaignControllerActionResponse> list(String accessToken, String campaignId, String version,
        String controllerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        ActionableCampaignStep stepWithActions = campaignStepProvider.getActionableStep(campaign, controllerId);

        List<CampaignControllerActionResponse> response = new ArrayList<>();
        for (CampaignControllerAction action : stepWithActions.getActions()) {
            CampaignControllerActionResponseMapper mapper = actionMapperRepository.getMapper(action.getType());
            CampaignControllerActionResponse actionResponse = mapper.toResponse(action, ZoneOffset.UTC);
            response.add(actionResponse);
        }

        return response;
    }

    @Override
    public CampaignControllerActionResponse read(String accessToken, String campaignId, String version,
        String controllerId, String actionId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerAction action = campaignStepProvider.getControllerAction(campaign, controllerId, actionId);
        CampaignControllerActionResponseMapper mapper = actionMapperRepository.getMapper(action.getType());

        return mapper.toResponse(action, ZoneOffset.UTC);
    }

}
