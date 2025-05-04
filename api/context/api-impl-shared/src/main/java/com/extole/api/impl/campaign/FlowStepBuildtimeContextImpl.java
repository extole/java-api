package com.extole.api.impl.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.FlowStepBuildtimeContext;

public class FlowStepBuildtimeContextImpl extends ExtendableCampaignBuildtimeContextImpl
    implements FlowStepBuildtimeContext {

    private final String name;
    private final String stepName;

    public FlowStepBuildtimeContextImpl(String name, String stepName, CampaignBuildtimeContext context) {
        super(context);
        this.name = name;
        this.stepName = stepName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

}
