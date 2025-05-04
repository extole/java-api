package com.extole.client.rest.impl.campaign.component.setting;

import com.extole.client.rest.campaign.configuration.CampaignComponentSettingConfiguration;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;

public interface ComponentSettingConfigurationMapper<TYPE extends CampaignComponentSettingConfiguration> {

    TYPE mapToSettingConfiguration(Setting setting);

    SettingType getSettingType();

}
