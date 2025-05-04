package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentDelayListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class DelayListVariableUploader implements SettingUploader<CampaignComponentDelayListVariableConfiguration> {

    private final DefaultSettingUploader defaultSettingUploader;

    DelayListVariableUploader(DefaultSettingUploader defaultSettingUploader) {
        this.defaultSettingUploader = defaultSettingUploader;
    }

    @Override
    public void upload(CampaignUploadContext context, CampaignComponentConfiguration component,
        CampaignComponentDelayListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        defaultSettingUploader.upload(context, component, variable);
    }

    @Override
    public void upload(CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentDelayListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        defaultSettingUploader.upload(context, socket, component, variable);
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.DELAY_LIST;
    }
}
