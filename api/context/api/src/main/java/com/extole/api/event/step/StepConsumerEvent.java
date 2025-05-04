package com.extole.api.event.step;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.event.ConsumerEvent;
import com.extole.api.event.ReferralContext;

@Schema
public interface StepConsumerEvent extends ConsumerEvent {

    String getName();

    String[] getAliases();

    boolean isFirstSiteVisit();

    @Nullable
    SelectedCampaignContext getSelectedCampaignContext();

    @Nullable
    ReferralContext getReferralContext();

    @Deprecated // TODO Remove ENG-22702
    @Nullable
    BigDecimal getValue();

    @Nullable
    PartnerEventId getPartnerEventId();

}
