package com.extole.api.event;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface InputConsumerEvent extends ConsumerEvent {

    @Deprecated // TODO Use getName() instead and remove it ENG-17657
    String getEventName();

    String getName();

    String getUrl();

    String getReferrer();

    List<String> getSourceIps();

    Map<String, List<String>> getHttpHeaders();

    Map<String, List<String>> getHttpCookies();

    List<String> getHandlerMessages();

    InputEventLabel[] getLabels();

    InputEventLocale getLocale();

}
