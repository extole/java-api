package com.extole.api.model.campaign.built;

import javax.annotation.Nullable;

public interface BuiltCampaignControllerTrigger {

    String getId();

    String getType();

    String getPhase();

    String getName();

    @Nullable
    String getDescription();

    boolean getEnabled();

    boolean getNegated();

    String getCreatedDate();

}
