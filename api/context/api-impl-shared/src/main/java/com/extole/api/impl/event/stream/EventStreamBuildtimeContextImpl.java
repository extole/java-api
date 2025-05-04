package com.extole.api.impl.event.stream;

import com.extole.api.campaign.ComponentBuildtimeContext;
import com.extole.api.event.stream.EventStreamBuildtimeContext;
import com.extole.api.impl.campaign.ExtendableComponentBuildtimeContextImpl;

public class EventStreamBuildtimeContextImpl extends ExtendableComponentBuildtimeContextImpl
    implements EventStreamBuildtimeContext {

    public EventStreamBuildtimeContextImpl(ComponentBuildtimeContext context) {
        super(context);
    }

}
