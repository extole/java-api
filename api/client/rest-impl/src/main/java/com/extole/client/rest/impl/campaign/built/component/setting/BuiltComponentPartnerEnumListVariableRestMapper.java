package com.extole.client.rest.impl.campaign.built.component.setting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltComponentPartnerEnumListVariableResponse;
import com.extole.client.rest.campaign.component.setting.PartnerEnumListVariableOptionResponse;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.id.Id;
import com.extole.model.entity.campaign.BuiltPartnerEnumListVariable;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltSetting;

@Component
public class BuiltComponentPartnerEnumListVariableRestMapper
    implements BuiltComponentSettingRestMapper<BuiltComponentPartnerEnumListVariableResponse> {
    @Override
    public BuiltComponentPartnerEnumListVariableResponse mapToSettingResponse(BuiltCampaign campaign,
        String componentId,
        BuiltSetting setting) {
        BuiltPartnerEnumListVariable partnerVariable = (BuiltPartnerEnumListVariable) setting;
        return new BuiltComponentPartnerEnumListVariableResponse(setting.getName(),
            setting.getDisplayName(),
            com.extole.client.rest.campaign.component.setting.SettingType.valueOf(setting.getType().name()),
            partnerVariable.getSourcedValues(),
            VariableSource.valueOf(partnerVariable.getSource().name()),
            partnerVariable.getDescription(),
            setting.getTags(),
            Id.valueOf(partnerVariable.getSourceComponentId().getValue()),
            partnerVariable.getSourceVersion(),
            partnerVariable.getPriority(),
            partnerVariable.getWebhookId().map(Id::getValue),
            partnerVariable.getOptions()
                .stream()
                .map(option -> new PartnerEnumListVariableOptionResponse(option.getId(), option.getName()))
                .collect(Collectors.toList()));

    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.PARTNER_ENUM_LIST);
    }
}
