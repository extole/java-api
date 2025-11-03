package com.extole.client.rest.impl.campaign.component.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.extole.client.rest.campaign.component.setting.SettingType;
import com.extole.client.rest.campaign.configuration.CampaignComponentConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentPartnerEnumListVariableConfiguration;
import com.extole.client.rest.campaign.configuration.CampaignComponentSocketConfiguration;
import com.extole.client.rest.campaign.configuration.PartnerEnumListVariableOptionConfiguration;
import com.extole.client.rest.impl.campaign.upload.CampaignUploadContext;
import com.extole.model.entity.campaign.PartnerEnumListVariableOption;
import com.extole.model.service.campaign.setting.PartnerEnumListVariableBuilder;
import com.extole.model.service.campaign.setting.SettingDisplayNameLengthException;
import com.extole.model.service.campaign.setting.SettingIllegalCharacterInDisplayNameException;
import com.extole.model.service.campaign.setting.SettingInvalidNameException;
import com.extole.model.service.campaign.setting.SettingNameLengthException;
import com.extole.model.service.campaign.setting.SettingTagLengthException;
import com.extole.model.service.campaign.setting.VariableValueKeyLengthException;

@Component
public class PartnerEnumListVariableUploader
    implements SettingUploader<CampaignComponentPartnerEnumListVariableConfiguration> {

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentConfiguration component,
        CampaignComponentPartnerEnumListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, component, variable);

        PartnerEnumListVariableBuilder variableBuilder =
            (PartnerEnumListVariableBuilder) context.get(component, variable);
        variableBuilder.withWebhookId(variable.getWebhookId());
        List<PartnerEnumListVariableOption> options = new ArrayList<>();
        for (PartnerEnumListVariableOptionConfiguration optionRequest : variable.getOptions()) {
            PartnerEnumListVariableOption option = PartnerEnumListVariableOption.builder()
                .withId(optionRequest.getId())
                .withName(optionRequest.getName())
                .build();
            options.add(option);
        }
        variableBuilder.withOptions(options);
    }

    @Override
    public void upload(SettingUploaderRegistry uploaderRegistry,
        CampaignUploadContext context, CampaignComponentSocketConfiguration socket,
        CampaignComponentConfiguration component, CampaignComponentPartnerEnumListVariableConfiguration variable)
        throws SettingNameLengthException, SettingInvalidNameException, SettingDisplayNameLengthException,
        SettingIllegalCharacterInDisplayNameException, VariableValueKeyLengthException, SettingTagLengthException {
        uploaderRegistry.getDefaultUploader().upload(uploaderRegistry, context, socket, component, variable);

        PartnerEnumListVariableBuilder variableBuilder =
            (PartnerEnumListVariableBuilder) context.get(component, socket, variable);
        variableBuilder.withWebhookId(variable.getWebhookId());
        List<PartnerEnumListVariableOption> options = new ArrayList<>();
        for (PartnerEnumListVariableOptionConfiguration optionRequest : variable.getOptions()) {
            PartnerEnumListVariableOption option = PartnerEnumListVariableOption.builder()
                .withId(optionRequest.getId())
                .withName(optionRequest.getName())
                .build();
            options.add(option);
        }
        variableBuilder.withOptions(options);
    }

    @Override
    public List<SettingType> getSettingTypes() {
        return Collections.singletonList(SettingType.PARTNER_ENUM_LIST);
    }
}
