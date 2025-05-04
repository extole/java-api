package com.extole.api.impl.campaign.component.install;

import java.time.ZoneId;
import java.util.Map;

import com.extole.api.campaign.component.install.Controller;
import com.extole.api.campaign.component.install.Trigger;
import com.extole.api.campaign.component.install.step.action.Action;
import com.extole.api.campaign.component.install.step.data.StepData;
import com.extole.api.impl.campaign.component.install.step.action.ActionToApiActionMapper;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaignController;

public class ControllerImpl implements Controller {

    private final String id;
    private final String name;
    private final String[] aliases;
    private final Action[] actions;
    private final Map<Id<?>, Id<?>> anchors;

    public ControllerImpl(ZoneId clientTimezone, BuiltCampaignController controller, Map<Id<?>, Id<?>> anchors) {
        this.id = controller.getId().getValue();
        this.name = controller.getName();
        this.aliases = controller.getAliases()
            .toArray(new String[0]);
        this.actions = controller.getActions().stream()
            .map(value -> ActionToApiActionMapper.map(clientTimezone, value))
            .toArray(Action[]::new);
        this.anchors = anchors;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public Action[] getActions() {
        return actions;
    }

    @Override
    public void anchor(Action action) {
        anchors.put(Id.valueOf(action.getId()), Id.valueOf(id));
    }

    @Override
    public void anchor(Trigger trigger) {
        anchors.put(Id.valueOf(trigger.getId()), Id.valueOf(id));
    }

    @Override
    public void anchor(StepData stepData) {
        anchors.put(Id.valueOf(stepData.getId()), Id.valueOf(id));
    }

}
