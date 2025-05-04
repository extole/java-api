package com.extole.client.rest.impl.campaign.component.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.CampaignComponentRestException;
import com.extole.client.rest.campaign.component.setting.SettingRestException;
import com.extole.client.rest.impl.campaign.component.CampaignComponentProvider;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.model.entity.campaign.Campaign;
import com.extole.model.entity.campaign.Setting;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;

@Component
public final class CampaignComponentSettingProvider {

    private final CampaignComponentProvider campaignComponentProvider;

    @Autowired
    CampaignComponentSettingProvider(CampaignComponentProvider campaignComponentProvider) {
        this.campaignComponentProvider = campaignComponentProvider;
    }

    public Setting getCampaignComponentSetting(Campaign campaign, String componentId, String name)
        throws CampaignComponentRestException, SettingRestException {
        return campaignComponentProvider.getCampaignComponent(componentId, campaign)
            .getSettings()
            .stream()
            .filter(setting -> setting.getName().equals(name))
            .findFirst().orElseThrow(() -> RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.SETTING_NOT_FOUND)
                .addParameter("setting_name", name)
                .addParameter("component_id", componentId)
                .build());
    }

    public List<Setting> getCampaignComponentSettings(Campaign campaign, String componentId)
        throws CampaignComponentRestException {
        return campaignComponentProvider.getCampaignComponent(componentId, campaign)
            .getSettings();
    }

    public BuiltSetting getBuiltCampaignComponentSetting(BuiltCampaign campaign, String componentId, String name)
        throws CampaignComponentRestException, SettingRestException {
        return campaignComponentProvider.getBuiltCampaignComponent(componentId, campaign)
            .getSettings()
            .stream()
            .filter(setting -> setting.getName().equals(name))
            .findFirst().orElseThrow(() -> RestExceptionBuilder.newBuilder(SettingRestException.class)
                .withErrorCode(SettingRestException.SETTING_NOT_FOUND)
                .addParameter("setting_name", name)
                .addParameter("component_id", componentId)
                .build());
    }

    public List<? extends BuiltSetting> getBuiltCampaignComponentSettings(BuiltCampaign campaign, String componentId)
        throws CampaignComponentRestException {
        return campaignComponentProvider.getBuiltCampaignComponent(componentId, campaign)
            .getSettings();
    }

}
