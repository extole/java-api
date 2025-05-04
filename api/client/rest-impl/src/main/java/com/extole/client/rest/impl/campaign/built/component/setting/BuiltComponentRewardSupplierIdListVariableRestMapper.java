package com.extole.client.rest.impl.campaign.built.component.setting;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.built.component.setting.BuiltComponentRewardSupplierIdListVariableResponse;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.id.Id;
import com.extole.model.entity.campaign.SettingType;
import com.extole.model.entity.campaign.built.BuiltCampaign;
import com.extole.model.entity.campaign.built.BuiltRewardSupplierIdListVariable;
import com.extole.model.entity.campaign.built.BuiltSetting;

@Component
public class BuiltComponentRewardSupplierIdListVariableRestMapper
    implements BuiltComponentSettingRestMapper<BuiltComponentRewardSupplierIdListVariableResponse> {

    @Override
    public BuiltComponentRewardSupplierIdListVariableResponse mapToSettingResponse(BuiltCampaign campaign,
        String componentId, BuiltSetting setting) {
        BuiltRewardSupplierIdListVariable variable = (BuiltRewardSupplierIdListVariable) setting;
        return new BuiltComponentRewardSupplierIdListVariableResponse(variable.getName(),
            variable.getDisplayName(),
            com.extole.client.rest.campaign.component.setting.SettingType.valueOf(variable.getType().name()),
            variable.getSourcedValues(),
            VariableSource.valueOf(variable.getSource().name()),
            variable.getDescription(),
            variable.getTags(),
            Id.valueOf(variable.getSourceComponentId().getValue()),
            variable.getPriority(),
            variable.getAllowedRewardSupplierIds().stream()
                .map(rewardSupplierId -> Id.valueOf(rewardSupplierId.getValue()))
                .collect(Collectors.toList()));

    }

    @Override
    public SettingType getSettingType() {
        return SettingType.REWARD_SUPPLIER_ID_LIST;
    }
}
