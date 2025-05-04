package com.extole.api.impl.model.campaign;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.ControllerBuildtimeContext;
import com.extole.api.model.campaign.ControllerTrigger;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;

final class ControllerTriggerImpl implements ControllerTrigger {
    private final com.extole.model.entity.campaign.CampaignControllerTrigger trigger;

    ControllerTriggerImpl(
        com.extole.model.entity.campaign.CampaignControllerTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public String getId() {
        return trigger.getId().getValue();
    }

    @Override
    public String getType() {
        return trigger.getType().name();
    }

    @Override
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getPhase() {
        return Evaluatables.remapClassToClass(trigger.getPhase(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getName() {
        return trigger.getName();
    }

    @Override
    public BuildtimeEvaluatable<ControllerBuildtimeContext, String> getDescription() {
        return Evaluatables.remapClassToClass(trigger.getDescription(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getEnabled() {
        return trigger.getEnabled();
    }

    @Override
    public String getCreatedDate() {
        return trigger.getCreatedDate().toString();
    }

    @Override
    public BuildtimeEvaluatable<ControllerBuildtimeContext, Boolean> getNegated() {
        return trigger.getNegated();
    }
}
