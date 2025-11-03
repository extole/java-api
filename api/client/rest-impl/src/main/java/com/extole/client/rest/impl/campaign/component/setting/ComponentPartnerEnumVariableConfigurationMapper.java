package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentPartnerEnumVariableConfiguration;
import com.extole.client.rest.campaign.configuration.PartnerEnumListVariableOptionConfiguration;
import com.extole.client.rest.campaign.configuration.VariableSource;
import com.extole.client.rest.impl.campaign.component.CampaignComponentRestMapperContext;
import com.extole.model.entity.campaign.PartnerEnumVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentPartnerEnumVariableConfigurationMapper
    implements ComponentSettingConfigurationMapper<CampaignComponentPartnerEnumVariableConfiguration> {
    @Override
    public CampaignComponentPartnerEnumVariableConfiguration mapToSettingConfiguration(
        CampaignComponentRestMapperContext restMapperContext, Setting setting) {
        PartnerEnumVariable partnerEnumVariable = (PartnerEnumVariable) setting;
        return new CampaignComponentPartnerEnumVariableConfiguration(setting.getName(),
            setting.getDisplayName(),
            SettingType.valueOf(setting.getType().name()),
            partnerEnumVariable.getValues(),
            VariableSource.valueOf(partnerEnumVariable.getSource().name()),
            partnerEnumVariable.getDescription(),
            setting.getTags(),
            setting.getPriority(),
            partnerEnumVariable.getWebhookId(),
            partnerEnumVariable.getOptions()
                .stream()
                .map(option -> new PartnerEnumListVariableOptionConfiguration(option.getId(), option.getName()))
                .collect(Collectors.toList()));
    }

    @Override
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return Collections.singletonList(com.extole.model.entity.campaign.SettingType.PARTNER_ENUM);
    }
}
