package com.extole.client.rest.impl.campaign.component.setting;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingResponse;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.SettingType;

public interface ComponentSettingRestMapper<TYPE extends CampaignComponentSettingResponse> {

    TYPE mapToSettingResponse(Setting setting);

    SettingType getSettingType();
}
