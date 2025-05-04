package com.extole.api.impl.prehandler.built;

import com.extole.api.campaign.ComponentBuildtimeContext;
import com.extole.api.impl.campaign.ExtendableComponentBuildtimeContextImpl;
import com.extole.api.prehandler.built.PrehandlerBuildtimeContext;

public class PrehandlerBuildtimeContextImpl extends ExtendableComponentBuildtimeContextImpl
    implements PrehandlerBuildtimeContext {

    public PrehandlerBuildtimeContextImpl(ComponentBuildtimeContext context) {
        super(context);
    }

}
