package com.extole.api.service;

import com.extole.api.campaign.Component;

public interface ComponentService {

    Component getComponentInCurrentCampaign(String componentId);
}
