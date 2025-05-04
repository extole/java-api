package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentRewardSupplierIdListVariableUpdateRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.RewardSupplierIdListVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class RewardSupplierIdListSettingUpdateRequestMapper
    implements SettingUpdateRequestMapper<CampaignComponentRewardSupplierIdListVariableUpdateRequest,
    RewardSupplierIdListVariableBuilder> {

    @Override
    public void complete(CampaignComponentRewardSupplierIdListVariableUpdateRequest updateRequest,
        RewardSupplierIdListVariableBuilder builder) throws VariableValueKeyLengthException {
        updateRequest.getValues().ifPresent(values -> builder.withValues(values));
        updateRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        updateRequest.getDescription().ifPresent(description -> builder.withDescription(description));
        updateRequest.getAllowedRewardSupplierIds()
            .ifPresent(rewardSupplierIdList -> builder.withAllowedRewardSupplierIdList(rewardSupplierIdList));
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.REWARD_SUPPLIER_ID_LIST;
    }

}
