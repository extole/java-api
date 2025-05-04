package com.extole.api.impl.campaign.component.install;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import com.extole.api.campaign.ComponentBuildtimeContext;
import com.extole.api.campaign.VariableContext;
import com.extole.api.campaign.component.install.ComponentInstalltimeContext;
import com.extole.api.campaign.component.install.SourceComponent;
import com.extole.api.campaign.component.install.TargetComponent;
import com.extole.id.Id;
import com.extole.model.entity.campaign.built.BuiltCampaignController;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerAction;
import com.extole.model.entity.campaign.built.BuiltCampaignControllerTrigger;
import com.extole.model.entity.campaign.built.BuiltCampaignJourneyEntry;
import com.extole.model.entity.campaign.built.BuiltStepData;

public class ComponentInstalltimeContextImpl implements ComponentInstalltimeContext {

    private final ComponentBuildtimeContext componentBuildtimeContext;
    private final SourceComponent sourceComponent;
    private final TargetComponent targetComponent;

    public ComponentInstalltimeContextImpl(
        ZoneId clientTimezone,
        Map<Id<?>, Id<?>> anchors,
        List<BuiltCampaignControllerTrigger> sourceTriggers,
        List<BuiltCampaignControllerTrigger> sourceUnanchoredTriggers,
        List<BuiltCampaignControllerAction> sourceActions,
        List<BuiltCampaignControllerAction> sourceUnanchoredActions,
        List<BuiltStepData> sourceStepData,
        List<BuiltStepData> sourceUnanchoredStepData,
        List<BuiltCampaignController> controllers,
        List<BuiltCampaignJourneyEntry> journeyEntries,
        ComponentBuildtimeContext componentBuildtimeContext) {
        this.sourceComponent = new SourceComponentImpl(clientTimezone, sourceTriggers, sourceUnanchoredTriggers,
            sourceActions, sourceUnanchoredActions, sourceStepData, sourceUnanchoredStepData);
        this.targetComponent = new TargetComponentImpl(clientTimezone, controllers, journeyEntries, anchors);
        this.componentBuildtimeContext = componentBuildtimeContext;
    }

    @Override
    public SourceComponent getSourceComponent() {
        return sourceComponent;
    }

    @Override
    public TargetComponent getTargetComponent() {
        return targetComponent;
    }

    @Override
    public VariableContext getVariableContext() {
        return componentBuildtimeContext.getVariableContext();
    }

    @Override
    public VariableContext getVariableContext(String defaultKey) {
        return componentBuildtimeContext.getVariableContext(defaultKey);
    }

    @Override
    public VariableContext getVariableContext(String... defaultKeys) {
        return componentBuildtimeContext.getVariableContext(defaultKeys);
    }

}
