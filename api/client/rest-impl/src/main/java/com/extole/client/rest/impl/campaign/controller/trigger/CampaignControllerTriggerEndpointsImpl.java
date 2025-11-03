package com.extole.client.rest.impl.campaign.controller.trigger;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.extole.authorization.service.Authorization;
import com.extole.client.rest.campaign.BuildCampaignRestException;
import com.extole.client.rest.campaign.CampaignRestException;
import com.extole.client.rest.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponse;
import com.extole.client.rest.campaign.controller.CampaignControllerRestException;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerEndpoints;
import com.extole.client.rest.campaign.controller.trigger.CampaignControllerTriggerResponse;
import com.extole.client.rest.impl.campaign.CampaignProvider;
import com.extole.client.rest.impl.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponseMapper;
import com.extole.client.rest.impl.campaign.built.controller.trigger.BuiltCampaignControllerTriggerResponseMapperRepository;
import com.extole.client.rest.impl.campaign.controller.CampaignStepProvider;
import com.extole.common.rest.exception.UserAuthorizationRestException;
import com.extole.common.rest.support.authorization.client.ClientAuthorizationProvider;
import com.extole.id.Id;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.CampaignControllerTrigger;
import com.extole.model.entity.campaign.CampaignStep;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.model.entity.campaign.built.BuiltCampaignStep;

@Provider
public class CampaignControllerTriggerEndpointsImpl implements CampaignControllerTriggerEndpoints {

    private final ClientAuthorizationProvider authorizationProvider;
    private final CampaignStepProvider campaignStepProvider;
    private final CampaignControllerTriggerResponseMapperRepository triggerMapperRepository;
    private final CampaignProvider campaignProvider;
    private final BuiltCampaignControllerTriggerResponseMapperRepository builtTriggerMapperRepository;

    @Inject
    public CampaignControllerTriggerEndpointsImpl(
        ClientAuthorizationProvider authorizationProvider,
        CampaignStepProvider campaignStepProvider,
        CampaignControllerTriggerResponseMapperRepository triggerMapperRepository,
        CampaignProvider campaignProvider,
        BuiltCampaignControllerTriggerResponseMapperRepository builtTriggerMapperRepository) {
        this.authorizationProvider = authorizationProvider;
        this.campaignStepProvider = campaignStepProvider;
        this.triggerMapperRepository = triggerMapperRepository;
        this.campaignProvider = campaignProvider;
        this.builtTriggerMapperRepository = builtTriggerMapperRepository;
    }

    @Override
    public List<CampaignControllerTriggerResponse> list(String accessToken, String campaignId, String version,
        String controllerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignStep step = campaignStepProvider.getStep(campaign, controllerId);

        return step.getTriggers().stream()
            .map(trigger -> {
                CampaignControllerTriggerResponseMapper mapper = triggerMapperRepository.getMapper(trigger.getType());
                return mapper.toResponse(trigger, ZoneOffset.UTC);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<BuiltCampaignControllerTriggerResponse> listBuilt(String accessToken, String campaignId, String version,
        String controllerId)
        throws UserAuthorizationRestException, CampaignRestException, CampaignControllerRestException,
        BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignStep step = campaignStepProvider.getBuiltStep(campaign, controllerId);

        return step.getTriggers().stream()
            .map(trigger -> {
                BuiltCampaignControllerTriggerResponseMapper mapper =
                    builtTriggerMapperRepository.getMapper(trigger.getType());
                return mapper.toResponse(trigger, ZoneOffset.UTC);
            })
            .collect(Collectors.toList());
    }

    @Override
    public CampaignControllerTriggerResponse get(String accessToken, String campaignId, String version,
        String controllerId, String triggerId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        Campaign campaign = campaignProvider.getCampaign(authorization, Id.valueOf(campaignId), version);
        CampaignControllerTrigger trigger = campaignStepProvider.getStepTrigger(campaign, controllerId, triggerId);

        CampaignControllerTriggerResponseMapper mapper = triggerMapperRepository.getMapper(trigger.getType());
        return mapper.toResponse(trigger, ZoneOffset.UTC);
    }

    @Override
    public BuiltCampaignControllerTriggerResponse getBuilt(String accessToken, String campaignId, String version,
        String controllerId, String triggerId) throws UserAuthorizationRestException, CampaignRestException,
        CampaignControllerRestException, BuildCampaignRestException {
        Authorization authorization = authorizationProvider.getClientAuthorization(accessToken);
        BuiltCampaign campaign = campaignProvider.getBuiltCampaign(authorization, Id.valueOf(campaignId), version);
        BuiltCampaignControllerTrigger trigger =
            campaignStepProvider.getBuiltStepTrigger(campaign, controllerId, triggerId);

        BuiltCampaignControllerTriggerResponseMapper mapper = builtTriggerMapperRepository.getMapper(trigger.getType());
        return mapper.toResponse(trigger, ZoneOffset.UTC);
    }

}
