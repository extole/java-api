package com.extole.api.impl.model.campaign;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.model.campaign.StepData;
import com.extole.api.step.data.StepDataContext;
import com.extole.evaluateable.BuildtimeEvaluatable;
import com.extole.evaluateable.Evaluatables;
import com.extole.evaluateable.RuntimeEvaluatable;

final class StepDataImpl implements StepData {
    private final com.extole.model.entity.campaign.StepData stepData;

    StepDataImpl(com.extole.model.entity.campaign.StepData stepData) {
        this.stepData = stepData;
    }

    @Override
    public String getId() {
        return stepData.getId().getValue();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getName() {
        return stepData.getName();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Object>>
        getValue() {
        return Evaluatables.remapClassToClass(stepData.getValue(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getScope() {
        return Evaluatables.remapClassToClass(stepData.getScope(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> isDimension() {
        return stepData.isDimension();
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, List<String>> getPersistTypes() {
        return Evaluatables.remapClassToClass(stepData.getPersistTypes(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, RuntimeEvaluatable<StepDataContext, Object>>
        getDefaultValue() {
        return Evaluatables.remapClassToClass(stepData.getDefaultValue(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, String> getKeyType() {
        return Evaluatables.remapClassToClass(stepData.getKeyType(), new TypeReference<>() {});
    }

    @Override
    public BuildtimeEvaluatable<CampaignBuildtimeContext, Boolean> getEnabled() {
        return stepData.getEnabled();
    }
}
