package com.extole.api.event.internal.reward;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface RewardContext {

    String getRewardId();

    String getName();

    @Nullable
    String getPartnerRewardId();

    @Deprecated // TODO should be removed after switch ENG-15542
    String[] getSlots();

    String[] getTags();

    BigDecimal getFaceValue();

    String getFaceValueType();

    BigDecimal getValueOfRewardedEvent();

    Map<String, String> getData();

    String getState();

    String getProgramLabel();

}
