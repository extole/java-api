package com.extole.api.impl.model.campaign;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.model.campaign.Controller;
import com.extole.api.model.campaign.ControllerAction;
import com.extole.api.model.campaign.ControllerTrigger;
import com.extole.api.model.campaign.StepData;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;

public final class ControllerImpl implements Controller {
    private final com.extole.model.entity.campaign.CampaignController campaignController;

    public ControllerImpl(com.extole.model.entity.campaign.CampaignController campaignController) {
        this.campaignController = campaignController;
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return campaignController.getName();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getScope() {
        return Evaluatables.remapClassToClass(campaignController.getScope(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getEnabledOnStates() {
        return Evaluatables.remapClassToClass(campaignController.getEnabledOnStates(), new TypeReference<>() {});
    }

    @Override
    public ControllerAction[] getActions() {
        return campaignController.getActions().stream()
            .map(value -> new ControllerActionImpl(value))
            .toArray(ControllerAction[]::new);
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> getSelectors() {
        return Evaluatables.remapClassToClass(campaignController.getSelectors(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getAliases() {
        return campaignController.getAliases();
    }

    @Override
    public StepData[] getData() {
        return campaignController.getData().stream()
            .map(value -> new StepDataImpl(value))
            .toArray(StepData[]::new);
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Set<String>> getJourneyNames() {
        return Evaluatables.remapClassToClass(campaignController.getJourneyNames(), new TypeReference<>() {});
    }

    @Override
    public String getType() {
        return campaignController.getType().name();
    }

    @Override
    public String getId() {
        return campaignController.getId().getValue();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled() {
        return campaignController.getEnabled();
    }

    @Override
    public ControllerTrigger[] getTriggers() {
        return campaignController.getTriggers().stream()
            .map(value -> new ControllerTriggerImpl(value))
            .toArray(ControllerTrigger[]::new);
    }

    @Override
    public String getCreatedDate() {
        return campaignController.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return campaignController.getUpdatedDate().toString();
    }
}
