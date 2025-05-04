package com.extole.client.rest.impl.campaign.component.setting;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.CampaignComponentRewardSupplierIdListVariableRequest;
import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.model.entity.campaign.VariableSource;
import com.extole.model.service.campaign.setting.RewardSupplierIdListVariableBuilder;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class RewardSupplierIdListCreateRequestMapper
    implements SettingCreateRequestMapper<CampaignComponentRewardSupplierIdListVariableRequest,
        RewardSupplierIdListVariableBuilder> {

    @Override
    public void complete(CampaignComponentRewardSupplierIdListVariableRequest createRequest,
        RewardSupplierIdListVariableBuilder builder) throws VariableValueKeyLengthException {
        createRequest.getAllowedRewardSupplierIds()
            .ifPresent(rewardSupplierIds -> builder.withAllowedRewardSupplierIdList(rewardSupplierIds));
        createRequest.getValues().ifPresent(values -> builder.withValues(values));
        createRequest.getSource().ifPresent(source -> builder.withSource(VariableSource.valueOf(source.name())));
        createRequest.getDescription().ifPresent(description -> builder.withDescription(description));
    }

    @Override
    public SettingType getSettingType() {
        return SettingType.REWARD_SUPPLIER_ID_LIST;
    }

}
