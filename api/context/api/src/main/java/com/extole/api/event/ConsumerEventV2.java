package com.extole.api.event;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Deprecated // TODO remove consumer event v2 ENG-7272
@Schema
public interface ConsumerEventV2 extends ConsumerEvent {

    @Nullable
    String getScheduleName();

    String getEventType();

    @Nullable
    String getChannel();

    String[] getRecipients();

}
