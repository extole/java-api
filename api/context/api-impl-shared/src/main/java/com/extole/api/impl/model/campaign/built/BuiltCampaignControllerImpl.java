package com.extole.api.impl.model.campaign.built;

import com.extole.api.model.campaign.built.BuiltCampaignController;
import com.extole.api.model.campaign.built.BuiltCampaignControllerAction;
import com.extole.api.model.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.api.model.campaign.built.BuiltStepData;

final class BuiltCampaignControllerImpl implements BuiltCampaignController {
    private final com.extole.model.entity.campaign.built.BuiltCampaignController builtCampaignController;

    BuiltCampaignControllerImpl(
        com.extole.model.entity.campaign.built.BuiltCampaignController builtCampaignController) {
        this.builtCampaignController = builtCampaignController;
    }

    @Override
    public String getName() {
        return builtCampaignController.getName();
    }

    @Override
    public String getScope() {
        return builtCampaignController.getScope().name();
    }

    @Override
    public String[] getEnabledOnStates() {
        return builtCampaignController.getEnabledOnStates().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public BuiltCampaignControllerAction[] getActions() {
        return builtCampaignController.getActions().stream()
            .map(value -> new BuiltCampaignControllerActionImpl(value))
            .toArray(BuiltCampaignControllerAction[]::new);
    }

    @Override
    public String[] getSelectors() {
        return builtCampaignController.getSelectors().stream()
            .map(value -> value.name())
            .toArray(String[]::new);
    }

    @Override
    public String[] getAliases() {
        return builtCampaignController.getAliases().toArray(String[]::new);
    }

    @Override
    public BuiltStepData[] getData() {
        return builtCampaignController.getData().stream()
            .map(value -> new BuiltStepDataImpl(value))
            .toArray(BuiltStepData[]::new);
    }

    @Override
    public String[] getJourneyNames() {
        return builtCampaignController.getJourneyNames().stream()
            .map(value -> value.getValue())
            .toArray(String[]::new);
    }

    @Override
    public String getType() {
        return builtCampaignController.getType().name();
    }

    @Override
    public String getId() {
        return builtCampaignController.getId().getValue();
    }

    @Override
    public boolean isEnabled() {
        return builtCampaignController.isEnabled();
    }

    @Override
    public BuiltCampaignControllerTrigger[] getTriggers() {
        return builtCampaignController.getTriggers().stream()
            .map(value -> new BuiltCampaignControllerTriggerImpl(value))
            .toArray(BuiltCampaignControllerTrigger[]::new);
    }

    @Override
    public String getCreatedDate() {
        return builtCampaignController.getCreatedDate().toString();
    }

    @Override
    public String getUpdatedDate() {
        return builtCampaignController.getUpdatedDate().toString();
    }
}
