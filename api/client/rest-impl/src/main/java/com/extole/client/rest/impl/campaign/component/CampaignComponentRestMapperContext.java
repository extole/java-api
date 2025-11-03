package com.extole.client.rest.impl.campaign.component;

import java.util.List;
import java.util.Map;

import com.extole.id.Id;
import com.extole.model.entity.campaign.CampaignComponent;

public record CampaignComponentRestMapperContext(Map<Id<CampaignComponent>, List<String>> absoluteComponentNames) {
}
