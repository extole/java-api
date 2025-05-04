package com.extole.api.impl.campaign.component.install.step.data;

import com.extole.api.campaign.component.install.step.data.StepData;
import com.extole.model.entity.campaign.built.BuiltStepData;

public class StepDataImpl implements StepData {

    private final String id;
    private final String name;

    public StepDataImpl(BuiltStepData builtStepData) {
        this.id = builtStepData.getId().getValue();
        this.name = builtStepData.getName();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

}
