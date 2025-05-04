package com.extole.api.impl.campaign.component.install;

import java.time.ZoneId;
import java.util.List;

import com.extole.api.campaign.component.install.SourceComponent;
import com.extole.api.campaign.component.install.Trigger;
import com.extole.api.campaign.component.install.step.action.Action;
import com.extole.api.campaign.component.install.step.data.StepData;
import com.extole.api.impl.campaign.component.install.step.action.ActionToApiActionMapper;
import com.extole.api.impl.campaign.component.install.step.data.StepDataImpl;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerAction;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.model.entity.campaign.built.BuiltStepData;

public class SourceComponentImpl implements SourceComponent {

    private final Trigger[] triggers;
    private final Trigger[] unanchoredTriggers;
    private final Action[] actions;
    private final Action[] unanchoredActions;
    private final StepData[] stepData;
    private final StepData[] unanchoredStepData;

    public SourceComponentImpl(
        ZoneId clientTimezone,
        List<BuiltCampaignControllerTrigger> triggers,
        List<BuiltCampaignControllerTrigger> unanchoredTriggers,
        List<BuiltCampaignControllerAction> actions,
        List<BuiltCampaignControllerAction> unanchoredActions,
        List<BuiltStepData> stepData,
        List<BuiltStepData> unanchoredStepData) {
        this.triggers = triggers.stream()
            .map(TriggerImpl::new)
            .toArray(Trigger[]::new);
        this.unanchoredTriggers = unanchoredTriggers.stream()
            .map(TriggerImpl::new)
            .toArray(Trigger[]::new);
        this.actions = actions.stream()
            .map(value -> ActionToApiActionMapper.map(clientTimezone, value))
            .toArray(Action[]::new);
        this.unanchoredActions = unanchoredActions.stream()
            .map(value -> ActionToApiActionMapper.map(clientTimezone, value))
            .toArray(Action[]::new);
        this.stepData = stepData.stream()
            .map(StepDataImpl::new)
            .toArray(StepData[]::new);
        this.unanchoredStepData = unanchoredStepData.stream()
            .map(StepDataImpl::new)
            .toArray(StepData[]::new);
    }

    @Override
    public Trigger[] getTriggers() {
        return triggers;
    }

    @Override
    public Trigger[] getUnanchoredTriggers() {
        return unanchoredTriggers;
    }

    @Override
    public Action[] getActions() {
        return actions;
    }

    @Override
    public Action[] getUnanchoredActions() {
        return unanchoredActions;
    }

    @Override
    public StepData[] getStepData() {
        return stepData;
    }

    @Override
    public StepData[] getUnanchoredStepData() {
        return unanchoredStepData;
    }

}
