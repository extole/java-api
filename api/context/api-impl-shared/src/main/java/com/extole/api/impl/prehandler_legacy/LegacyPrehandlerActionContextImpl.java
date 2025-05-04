package com.extole.api.impl.prehandler_legacy;

import com.extole.api.prehandler_legacy.LegacyPrehandlerActionContext;

@Deprecated // TODO to be removed in ENG-13399
public final class LegacyPrehandlerActionContextImpl implements LegacyPrehandlerActionContext {
    private final LegacyPrehandlerEventBuilder eventBuilder;

    public LegacyPrehandlerActionContextImpl(LegacyPrehandlerEventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }

    @Override
    public LegacyPrehandlerEventBuilder getEventBuilder() {
        return eventBuilder;
    }
}
