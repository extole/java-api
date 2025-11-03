package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;

import com.extole.client.rest.campaign.component.setting.CampaignComponentSettingUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.service.campaign.setting.SettingBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

public interface SettingUpdateRequestMapper<REQUEST extends CampaignComponentSettingUpdateRequest, BUILDER extends SettingBuilder> {

    void complete(REQUEST updateRequest, BUILDER builder) throws VariableValueKeyLengthException,
        SettingIllegalCharacterInDisplayNameException, SettingTagLengthException, SettingDisplayNameLengthException,
        SettingNameLengthException, SettingInvalidNameException;

    List<SettingType> getSettingTypes();

}
