package com.extole.api.event;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ReferralContext {

    String getMySide();

    String getOtherPersonSide();

    String getOtherPersonId();

    String getReason();

    Map<String, Object> getData();

}
