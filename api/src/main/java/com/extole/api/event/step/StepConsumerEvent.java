package com.extole.api.event.step;

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
}
