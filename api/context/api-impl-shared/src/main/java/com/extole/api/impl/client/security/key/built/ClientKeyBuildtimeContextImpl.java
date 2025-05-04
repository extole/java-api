package com.extole.api.impl.client.security.key.built;

import com.extole.api.campaign.ComponentBuildtimeContext;
import com.extole.api.client.security.key.built.ClientKeyBuildtimeContext;
import com.extole.api.impl.campaign.ExtendableComponentBuildtimeContextImpl;

public class ClientKeyBuildtimeContextImpl extends ExtendableComponentBuildtimeContextImpl
    implements ClientKeyBuildtimeContext {

    public ClientKeyBuildtimeContextImpl(ComponentBuildtimeContext context) {
        super(context);
    }
}
