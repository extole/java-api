package com.extole.client.rest.impl.campaign.component.setting;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentRewardSupplierIdListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.model.service.campaign.setting.RewardSupplierIdListVariableBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class RewardSupplierIdListUploader implements
    SettingUploader<CampaignComponentRewardSupplierIdListVariableConfiguration> {

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentConfiguration component,
        CampaignComponentRewardSupplierIdListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, component, variable);

        RewardSupplierIdListVariableBuilder variableBuilder =
            (RewardSupplierIdListVariableBuilder) context.get(component, variable);

        variableBuilder.withAllowedRewardSupplierIdList(variable.getAllowedRewardSupplierIds());
    }

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentRewardSupplierIdListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, socket, component, variable);

        RewardSupplierIdListVariableBuilder variableBuilder =
            (RewardSupplierIdListVariableBuilder) context.get(component, variable);

        variableBuilder.withAllowedRewardSupplierIdList(variable.getAllowedRewardSupplierIds());
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.REWARD_SUPPLIER_ID_LIST);
    }
}
