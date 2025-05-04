package com.extole.client.rest.impl.campaign.built.component.setting;

import com.extole.client.rest.campaign.built.component.setting.BuiltCampaignComponentSettingResponse;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;

public interface BuiltComponentSettingRestMapper<TYPE extends BuiltCampaignComponentSettingResponse> {

    TYPE mapToSettingResponse(BuiltCampaign campaign, String componentId, BuiltSetting setting);

    SettingType getSettingType();
}
