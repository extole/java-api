package com.extole.api.impl.campaign;

import com.extole.model.entity.campaign.CampaignComponent;

public interface ComponentWithVersion {

    CampaignComponent getComponent();

    Integer getReferenceVersion();
}
