package com.extole.api.impl.webhook.built;

import com.extole.api.campaign.ComponentBuildtimeContext;
import com.extole.api.impl.campaign.ExtendableComponentBuildtimeContextImpl;
import com.extole.api.webhook.built.WebhookBuildtimeContext;

public class WebhookBuildtimeContextImpl extends ExtendableComponentBuildtimeContextImpl
    implements WebhookBuildtimeContext {

    public WebhookBuildtimeContextImpl(ComponentBuildtimeContext context) {
        super(context);
    }
}
