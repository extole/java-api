package com.extole.api.impl.audience.built;

import com.extole.api.audience.AudienceBuildtimeContext;
import com.extole.api.campaign.ComponentBuildtimeContext;
import com.extole.api.impl.campaign.ExtendableComponentBuildtimeContextImpl;

public class AudienceBuildtimeContextImpl extends ExtendableComponentBuildtimeContextImpl
    implements AudienceBuildtimeContext {

    public AudienceBuildtimeContextImpl(ComponentBuildtimeContext context) {
        super(context);
    }

}
