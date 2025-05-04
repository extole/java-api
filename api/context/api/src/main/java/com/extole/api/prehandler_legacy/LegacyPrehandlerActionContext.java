package com.extole.api.prehandler_legacy;

@Deprecated // TODO to be removed in ENG-13399
public interface LegacyPrehandlerActionContext {

    LegacyPrehandlerEventBuilder getEventBuilder();

    @Deprecated // TODO to be removed in ENG-13399
    interface LegacyPrehandlerEventBuilder {
        LegacyPrehandlerEventBuilder withEventName(String eventName);

        LegacyPrehandlerEventBuilder withEventTime(String eventTime);

        LegacyPrehandlerEventBuilder addData(String key, Object value);

        LegacyPrehandlerEventBuilder removeData(String key);

        LegacyPrehandlerEventBuilder log(String message);
    }
}
