package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;

import com.extole.client.rest.campaign.configuration.CampaignComponentSettingConfiguration;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapperContext;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;

public interface ComponentSettingConfigurationMapper<TYPE extends CampaignComponentSettingConfiguration> {

    TYPE mapToSettingConfiguration(CampaignComponentRestMapperContext restMapperContext, Setting setting);

    List<SettingType> getSettingTypes();

}
