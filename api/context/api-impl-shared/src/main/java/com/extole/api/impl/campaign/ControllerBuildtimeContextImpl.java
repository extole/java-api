package com.extole.api.impl.campaign;

import com.extole.api.campaign.CampaignBuildtimeContext;
import com.extole.api.campaign.ControllerBuildtimeContext;

public class ControllerBuildtimeContextImpl extends ExtendableCampaignBuildtimeContextImpl
    implements ControllerBuildtimeContext {
    private final String controllerName;

    public ControllerBuildtimeContextImpl(String controllerName, CampaignBuildtimeContext context) {
        super(context);
        this.controllerName = controllerName;
    }

    @Override
    public final String getControllerName() {
        return controllerName;
    }

}
