package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentRewardSupplierIdListVariableResponse;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.component.setting.VariableSource;
import com.extole.model.entity.campaign.RewardSupplierIdListVariable;
import com.extole.model.entity.campaign.Setting;

@Component
public class ComponentRewardSupplierIdListRestMapper
    implements ComponentSettingRestMapper<CampaignComponentRewardSupplierIdListVariableResponse> {

    @Override
    public CampaignComponentRewardSupplierIdListVariableResponse mapToSettingResponse(Setting setting) {
        RewardSupplierIdListVariable rewardSupplierIdListVariable = (RewardSupplierIdListVariable) setting;
        return new CampaignComponentRewardSupplierIdListVariableResponse(
            rewardSupplierIdListVariable.getName(),
            rewardSupplierIdListVariable.getDisplayName(),
            SettingType.valueOf(rewardSupplierIdListVariable.getType().name()),
            rewardSupplierIdListVariable.getValues(),
            VariableSource.valueOf(rewardSupplierIdListVariable.getSource().name()),
            rewardSupplierIdListVariable.getDescription(),
            rewardSupplierIdListVariable.getTags(),
            rewardSupplierIdListVariable.getPriority(),
            rewardSupplierIdListVariable.getAllowedRewardSupplierIds());

    }

    @Override
    public List<com.extole.model.entity.campaign.SettingType> getSettingTypes() {
        return Collections.singletonList(com.extole.model.entity.campaign.SettingType.REWARD_SUPPLIER_ID_LIST);
    }

}
