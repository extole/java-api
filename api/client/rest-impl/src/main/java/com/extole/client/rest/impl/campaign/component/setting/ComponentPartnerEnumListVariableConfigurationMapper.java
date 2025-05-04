package com.extole.client.rest.impl.campaign.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentPartnerEnumListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.PartnerEnumListVariableOptionConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.model.entity.campaign.PartnerEnumListVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentPartnerEnumListVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentPartnerEnumListVariableConfiguration> {
    @Override
    public CampaignComponentPartnerEnumListVariableConfiguration mapToSettingConfiguration(Setting setting) {
        PartnerEnumListVariable partnerEnumListVariable = (PartnerEnumListVariable) setting;
        return new CampaignComponentPartnerEnumListVariableConfiguration(setting.getName(),
            setting.getDisplayName(),
            SettingType.valueOf(setting.getType().name()),
            partnerEnumListVariable.getValues(),
            VariableSource.valueOf(partnerEnumListVariable.getSource().name()),
            partnerEnumListVariable.getDescription(),
            setting.getTags(),
            setting.getPriority(),
            partnerEnumListVariable.getWebhookId(),
            partnerEnumListVariable.getOptions()
                .stream()
                .map(option -> new PartnerEnumListVariableOptionConfiguration(option.getId(), option.getName(),
                    option.getDefault()))
                .collect(Collectors.toList()));

    }

    @Override
    public com.extole.model.entity.campaign.SettingType getSettingType() {
        return com.extole.model.entity.campaign.SettingType.PARTNER_ENUM_LIST;
    }

}
